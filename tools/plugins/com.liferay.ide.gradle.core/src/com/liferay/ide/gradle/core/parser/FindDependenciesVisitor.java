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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

/**
 * @author Lovett Li
 */
public class FindDependenciesVisitor extends CodeVisitorSupport {

	public int getColumnNum() {
		return _columnNum;
	}

	public int getDependenceLineNum() {
		return _dependenceLineNum;
	}

	public List<GradleDependency> getDependencies() {
		return _dependencies;
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		List<Expression> expressions = ale.getExpressions();

		if ((expressions.size() == 1) && (expressions.get(0) instanceof ConstantExpression)) {
			String depStr = expressions.get(0).getText();

			String[] deps = depStr.split(":");

			if (deps.length == 3) {
				_dependencies.add(new GradleDependency(deps[0], deps[1], deps[2]));
			}
		}

		super.visitArgumentlistExpression(ale);
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		if ((_dependenceLineNum != -1) && (expression.getLineNumber() == expression.getLastLineNumber())) {
			_columnNum = expression.getLastColumnNumber();
		}

		super.visitClosureExpression(expression);
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		List<MapEntryExpression> mapEntryExpressions = expression.getMapEntryExpressions();
		Map<String, String> dependenceMap = new HashMap<>();

		for (MapEntryExpression mapEntryExpression : mapEntryExpressions) {
			String key = mapEntryExpression.getKeyExpression().getText();
			String value = mapEntryExpression.getValueExpression().getText();

			dependenceMap.put(key, value);
		}

		_dependencies.add(new GradleDependency(dependenceMap));

		super.visitMapExpression(expression);
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		if (!call.getMethodAsString().equals("buildscript")) {
			if (call.getMethodAsString().equals("dependencies")) {
				if (_dependenceLineNum == -1) {
					_dependenceLineNum = call.getLastLineNumber();
				}
			}

			super.visitMethodCallExpression(call);
		}
	}

	private int _columnNum = -1;
	private int _dependenceLineNum = -1;
	private List<GradleDependency> _dependencies = new ArrayList<>();

}