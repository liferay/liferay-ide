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

package com.liferay.ide.gradle.core.model;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
public class UnusedDependenciesClosureVisitor extends CodeVisitorSupport {

	public UnusedDependenciesClosureVisitor() {
		_buildscript = false;
	}

	public UnusedDependenciesClosureVisitor(boolean buildscript) {
		_buildscript = buildscript;
	}

	public int[] getDependenceLineNumbers(Artifact artifact) {
		int[] lineNumbers = new int[0];

		Set<Artifact> dependencyArtifacts = _dependencyArtifactsToLineNumbers.keySet();

		Optional<Artifact> optional = dependencyArtifacts.stream(
		).filter(
			dependency -> StringUtil.equals(dependency.getConfiguration(), artifact.getConfiguration())
		).filter(
			dependency -> StringUtil.equals(dependency.getGroupId(), artifact.getGroupId())
		).filter(
			dependency -> StringUtil.equals(dependency.getArtifactId(), artifact.getArtifactId())
		).findFirst();

		if (optional.isPresent()) {
			Artifact dependencyArtifact = optional.get();

			lineNumbers = _dependencyArtifactsToLineNumbers.get(dependencyArtifact);
		}

		return lineNumbers;
	}

	public List<Artifact> getDependencies(String configuration) {
		if ("*".equals(configuration)) {
			return _dependencyArtifacts;
		}

		Stream<Artifact> stream = _dependencyArtifacts.stream();

		return stream.filter(
			artifact -> configuration.equals(artifact.getConfiguration())
		).collect(
			Collectors.toList()
		);
	}

	public int getDependenciesLineNumber() {
		return _dependenciesLineNumber;
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

			if (groups.length < 2) {
				return;
			}

			String version = (groups.length > 2) ? groups[2] : "";

			Artifact artifact = new Artifact();

			artifact.setGroupId(groups[0]);
			artifact.setArtifactId(groups[1]);
			artifact.setVersion(version);
			artifact.setConfiguration(_configurationName);

			_dependencyArtifacts.add(artifact);

			if (artifact.validate()) {
				int[] lineNumbers = new int[2];

				lineNumbers[0] = _startLineNumber;
				lineNumbers[1] = _endLineNumber;

				_dependencyArtifactsToLineNumbers.put(artifact, lineNumbers);
			}

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

			if (artifact.validate()) {
				artifact.setConfiguration(_configurationName);

				_dependencyArtifacts.add(artifact);

				int[] lineNumbers = new int[2];

				lineNumbers[0] = _startLineNumber;
				lineNumbers[1] = _endLineNumber;

				_dependencyArtifactsToLineNumbers.put(artifact, lineNumbers);
			}

			super.visitMapExpression(expression);
		}
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
		String methodString = methodCallExpression.getMethodAsString();

		if ("dependencies".equals(methodString)) {
			_dependenciesClosure = true;

			if (_dependenciesLineNumber == -1) {
				_dependenciesLineNumber = methodCallExpression.getLastLineNumber();
			}

			super.visitMethodCallExpression(methodCallExpression);

			_dependenciesClosure = false;
		}
		else if (_buildscript && "buildscript".equals(methodString)) {
			super.visitMethodCallExpression(methodCallExpression);
		}
		else if (_dependenciesClosure && _dependencyStatement) {
			_configurationName = methodString;

			_startLineNumber = methodCallExpression.getLineNumber();
			_endLineNumber = methodCallExpression.getLastLineNumber();

			super.visitMethodCallExpression(methodCallExpression);

			_configurationName = "";
		}
	}

	private boolean _buildscript;
	private String _configurationName = "";
	private boolean _dependenciesClosure = false;
	private int _dependenciesLineNumber = -1;
	private List<Artifact> _dependencyArtifacts = new ArrayList<>();
	private Map<Artifact, int[]> _dependencyArtifactsToLineNumbers = new HashMap<>();
	private boolean _dependencyStatement = false;
	private int _endLineNumber = -1;
	private int _startLineNumber = -1;

}