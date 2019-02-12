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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import com.liferay.ide.core.Artifact;

import java.util.HashMap;
import java.util.Map;

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
 */
public class DependenciesClosureVisitor extends CodeVisitorSupport {

	public ListMultimap<String, Artifact> getDependencies() {
		return _dependencies;
	}

	/**
	 * parse "group:name:version:classifier"
	 */
	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		if ("".equals(_configurationName)) {
			super.visitArgumentlistExpression(ale);
		}
		else {
			String text = _getExpressionText(ale.getExpression(0));

			String[] groups = text.split(":");

			String version = groups.length > 2 ? groups[2] : null;

			_dependencies.put(_configurationName, new Artifact(groups[0], groups[1], version));

			super.visitArgumentlistExpression(ale);
		}
	}

	@Override
	public void visitBlockStatement(BlockStatement block) {
		if (_dependenciesClosure) {
			_dependencyStatement = true;
			super.visitBlockStatement(block);

			_dependencyStatement = false;
		}
		else {
			super.visitBlockStatement(block);
		}
	}

	/**
	 * parse "configurationName group: group:, name: name, version: version"
	 */
	@Override
	public void visitMapExpression(MapExpression expression) {
		if ("".equals(_configurationName)) {
			super.visitMapExpression(expression);
		}
		else {
			Map<String, String> dependenceMap = new HashMap<>();

			for (MapEntryExpression mapEntryExpression : expression.getMapEntryExpressions()) {
				String key = _getExpressionText(mapEntryExpression.getKeyExpression());

				String value = _getExpressionText(mapEntryExpression.getValueExpression());

				dependenceMap.put(key, value);
			}

			_dependencies.put(_configurationName, new Artifact(dependenceMap));

			super.visitMapExpression(expression);
		}
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		if ("dependencies".equals(call.getMethodAsString())) {
			_dependenciesClosure = true;
			super.visitMethodCallExpression(call);

			_dependenciesClosure = false;
		}
		else if ("buildscript".equals(call.getMethodAsString())) {
			super.visitMethodCallExpression(call);
		}
		else if (_dependenciesClosure && _dependencyStatement) {
			_configurationName = call.getMethodAsString();
			super.visitMethodCallExpression(call);
			_configurationName = "";
		}
	}

	private String _getExpressionText(Expression expression) {
		return expression.getText();
	}

	private String _configurationName = "";
	private ListMultimap<String, Artifact> _dependencies = MultimapBuilder.hashKeys().arrayListValues().build();
	private boolean _dependenciesClosure = false;
	private boolean _dependencyStatement = false;

}