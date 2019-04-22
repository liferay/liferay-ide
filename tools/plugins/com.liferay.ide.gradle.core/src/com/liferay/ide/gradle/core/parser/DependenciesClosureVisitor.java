/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.gradle.core.parser;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;

/**
 * Visit gradle dependencies closure to collect all dependencies' info
 *
 * @author Charles Wu
 * @author Terry Jia
 * @author Simon Jiang
 */
public class DependenciesClosureVisitor extends CodeVisitorSupport {

	public DependenciesClosureVisitor() {
		_buildscript = false;
	}

	public DependenciesClosureVisitor(boolean buildscript) {
		_buildscript = buildscript;
	}

	public int getDependenceLineNumber() {
		return _dependenceLineNumber;
	}

	public List<Artifact> getDependencies(String configuration) {
		if ("*".equals(configuration)) {
			return _dependencies;
		}

		Stream<Artifact> stream = _dependencies.stream();

		return stream.filter(
			artifact -> configuration.equals(artifact.getConfiguration())
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * parse "group:name:version:classifier"
	 */
	@Override
	public void visitArgumentlistExpression(ArgumentListExpression argumentListExpression) {
		if (CoreUtil.isNullOrEmpty(_configurationName)) {
			super.visitArgumentlistExpression(argumentListExpression);
		}
		else {
			Expression expression = argumentListExpression.getExpression(0);

			String text = expression.getText();

			String[] groups = text.split(":");

			String version = (groups.length > 2) ? groups[2] : "";

			Artifact artifact = new Artifact();

			artifact.setGroupId(groups[0]);
			artifact.setArtifactId(groups[1]);
			artifact.setVersion(version);
			artifact.setConfiguration(_configurationName);

			_dependencies.add(artifact);

			super.visitArgumentlistExpression(argumentListExpression);
		}
	}

	@Override
	public void visitBlockStatement(BlockStatement blockStatement) {
		if (_dependenciesClosure) {
			_dependencyStatement = true;

			super.visitBlockStatement(blockStatement);

			_dependencyStatement = false;
		}
		else {
			super.visitBlockStatement(blockStatement);
		}
	}

	/**
	 * parse "configuration Name group: group:, name: name, version: version"
	 */
	@Override
	public void visitMapExpression(MapExpression expression) {
		if (CoreUtil.isNullOrEmpty(_configurationName)) {
			super.visitMapExpression(expression);
		}
		else {
			Artifact artifact = new Artifact();

			for (MapEntryExpression mapEntryExpression : expression.getMapEntryExpressions()) {
				Expression keyExpression = mapEntryExpression.getKeyExpression();

				String key = keyExpression.getText();

				Expression valueExpression = mapEntryExpression.getValueExpression();

				String value = valueExpression.getText();

				if ("group".equals(key)) {
					artifact.setGroupId(value);
				}
				else if ("name".equals(key)) {
					artifact.setArtifactId(value);
				}
				else if ("version".equals(key)) {
					artifact.setVersion(value);
				}
			}

			artifact.setConfiguration(_configurationName);
			artifact.setConfigurationStartLineNumber(_configurationStartLineNumber);
			artifact.setConfigurationEndLineNumber(_configurationEndLineNumber);

			_dependencies.add(artifact);

			super.visitMapExpression(expression);
		}
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
		String methodString = methodCallExpression.getMethodAsString();

		if ("dependencies".equals(methodString)) {
			_dependenciesClosure = true;

			if (_dependenceLineNumber == -1) {
				_dependenceLineNumber = methodCallExpression.getLastLineNumber();
			}

			super.visitMethodCallExpression(methodCallExpression);

			_dependenciesClosure = false;
		}
		else if (_buildscript && "buildscript".equals(methodString)) {
			super.visitMethodCallExpression(methodCallExpression);
		}
		else if (_dependenciesClosure && _dependencyStatement) {
			_configurationName = methodString;

			_configurationStartLineNumber = methodCallExpression.getLineNumber();
			_configurationEndLineNumber = methodCallExpression.getLastLineNumber();

			super.visitMethodCallExpression(methodCallExpression);

			_configurationName = "";
		}
	}

	private boolean _buildscript;
	private int _configurationEndLineNumber = -1;
	private String _configurationName = "";
	private int _configurationStartLineNumber = -1;
	private int _dependenceLineNumber = -1;
	private List<Artifact> _dependencies = new ArrayList<>();
	private boolean _dependenciesClosure = false;
	private boolean _dependencyStatement = false;

}