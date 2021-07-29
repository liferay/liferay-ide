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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.syntax.Token;

/**
 * @author Vernon Singleton
 * @author Gregory Amerson
 * @author Seiphon Wang
 */
public class BuildScriptVisitor extends CodeVisitorSupport {

	public List<GradleDependency> getBuildscriptDependencies() {
		return _buildscriptDependencies;
	}

	public List<GradleDependency> getDependencies() {
		return _dependencies;
	}

	public int getDependenciesLastLineNumber() {
		return _dependenciesLastLineNumber;
	}

	public List<String> getWarCoreExtDefaultConfiguration() {
		return _warCoreExtDefaultConfiguration;
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression argumentListExpression) {
		List<Expression> expressions = argumentListExpression.getExpressions();

		if ((expressions.size() == 1) && (expressions.get(0) instanceof ConstantExpression)) {
			ConstantExpression constantExpression = (ConstantExpression)expressions.get(0);

			constantExpression.getLineNumber();

			String expressionText = constantExpression.getText();

			String[] deps = expressionText.split(":");

			if (deps.length >= 3) {
				GradleDependency gradleDependency = new GradleDependency(
					_configurationName, deps[0], deps[1], deps[2], constantExpression.getLineNumber(),
					constantExpression.getLastLineNumber(), null);

				if (_inDependencies && !_inBuildscriptDependencies) {
					_dependencies.add(gradleDependency);
				}

				if (_inBuildscriptDependencies) {
					_buildscriptDependencies.add(gradleDependency);
				}
			}
		}
		else if ((expressions.size() == 2) && (expressions.get(1) instanceof ClosureExpression)) {
			if (_inDependencies || _inBuildscriptDependencies) {
				_hasArguments = true;
			}
		}

		super.visitArgumentlistExpression(argumentListExpression);
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		if (_inEclipse && _inClasspath && (_blockStatementStack.isEmpty() ? false : _blockStatementStack.peek())) {
			Expression leftExpression = expression.getLeftExpression();
			Expression rightExpression = expression.getRightExpression();
			Token operation = expression.getOperation();

			_warCoreExtDefaultConfiguration.add(leftExpression.getText());
			_warCoreExtDefaultConfiguration.add(operation.getText());
			_warCoreExtDefaultConfiguration.add(rightExpression.getText());
		}

		super.visitBinaryExpression(expression);
	}

	@Override
	public void visitBlockStatement(BlockStatement blockStatement) {
		if (_inDependencies || _inBuildscriptDependencies || (_inEclipse && _inClasspath)) {
			_blockStatementStack.push(true);

			super.visitBlockStatement(blockStatement);

			_blockStatementStack.pop();
		}
		else {
			super.visitBlockStatement(blockStatement);
		}
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		if (_hasArguments) {
			_inArguments = true;
		}

		super.visitClosureExpression(expression);

		if ((_inDependencies || _inBuildscriptDependencies) && _hasArguments) {
			_inArguments = false;

			_hasArguments = false;

			GradleDependency dependencyWithArguments = _buildscriptDependencies.get(
				_buildscriptDependencies.size() - 1);

			dependencyWithArguments.setArguments(_arguments);

			dependencyWithArguments.setLastLineNumber(expression.getLastLineNumber());

			_arguments = new ArrayList<>();
		}
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		List<MapEntryExpression> mapEntryExpressions = expression.getMapEntryExpressions();
		Map<String, String> dependencyMap = new HashMap<>();

		boolean gav = false;

		for (MapEntryExpression mapEntryExpression : mapEntryExpressions) {
			Expression keyExpression = mapEntryExpression.getKeyExpression();
			Expression valueExpression = mapEntryExpression.getValueExpression();

			String key = keyExpression.getText();
			String value = valueExpression.getText();

			if (key.equalsIgnoreCase("group")) {
				gav = true;
			}

			dependencyMap.put(key, value);
		}

		if (gav) {
			if (_inDependencies && !_inBuildscriptDependencies) {
				if (_hasArguments && _inArguments) {
					_arguments.add(
						new GradleDependency(
							_configurationName, dependencyMap.get("group"), dependencyMap.get("module"),
							dependencyMap.get("version"), expression.getLineNumber(), expression.getLastLineNumber(),
							null));
				}
				else {
					_dependencies.add(
						new GradleDependency(
							_configurationName, dependencyMap.get("group"), dependencyMap.get("name"),
							dependencyMap.get("version"), expression.getLineNumber(), expression.getLastLineNumber(),
							null));
				}
			}

			if (_inBuildscriptDependencies) {
				if (_hasArguments && _inArguments) {
					_arguments.add(
						new GradleDependency(
							_configurationName, dependencyMap.get("group"), dependencyMap.get("module"),
							dependencyMap.get("version"), expression.getLineNumber(), expression.getLastLineNumber(),
							null));
				}
				else {
					_buildscriptDependencies.add(
						new GradleDependency(
							_configurationName, dependencyMap.get("group"), dependencyMap.get("name"),
							dependencyMap.get("version"), expression.getLineNumber(), expression.getLastLineNumber(),
							null));
				}
			}
		}

		super.visitMapExpression(expression);
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		int lineNumber = call.getLineNumber();

		if (lineNumber > _dependenciesLastLineNumber) {
			_inDependencies = false;
		}

		if (lineNumber > _buildscriptLastLineNumber) {
			_inBuildscript = false;
		}

		if (lineNumber > _buildscriptDependenciesLastLineNumber) {
			_inBuildscriptDependencies = false;
		}

		String method = call.getMethodAsString();

		if (method.equals("buildscript")) {
			_buildscriptLastLineNumber = call.getLastLineNumber();

			_inBuildscript = true;
		}

		if (method.equals("dependencies")) {
			_dependenciesLastLineNumber = call.getLastLineNumber();

			_inDependencies = true;
		}

		if (method.equals("eclipse")) {
			_dependenciesLastLineNumber = call.getLastLineNumber();

			_inEclipse = true;
		}

		if (method.equals("classpath") && _inEclipse) {
			_dependenciesLastLineNumber = call.getLastLineNumber();

			_inClasspath = true;
		}

		if (_inBuildscript && _inDependencies && (_buildscriptDependenciesLastLineNumber == -1)) {
			_buildscriptDependenciesLastLineNumber = call.getLastLineNumber();
			_inBuildscriptDependencies = true;
		}

		if ((_inDependencies || _inBuildscriptDependencies) &&
			(_blockStatementStack.isEmpty() ? false : _blockStatementStack.peek())) {

			_configurationName = method;

			super.visitMethodCallExpression(call);

			_configurationName = null;
		}
		else {
			super.visitMethodCallExpression(call);
		}
	}

	private List<GradleDependency> _arguments = new ArrayList<>();
	private Stack<Boolean> _blockStatementStack = new Stack<>();
	private List<GradleDependency> _buildscriptDependencies = new ArrayList<>();
	private int _buildscriptDependenciesLastLineNumber = -1;
	private int _buildscriptLastLineNumber = -1;
	private String _configurationName;
	private List<GradleDependency> _dependencies = new ArrayList<>();
	private int _dependenciesLastLineNumber = -1;
	private boolean _hasArguments = false;
	private boolean _inArguments = false;
	private boolean _inBuildscript = false;
	private boolean _inBuildscriptDependencies = false;
	private boolean _inClasspath = false;
	private boolean _inDependencies = false;
	private boolean _inEclipse = false;
	private List<String> _warCoreExtDefaultConfiguration = new ArrayList<>();

}