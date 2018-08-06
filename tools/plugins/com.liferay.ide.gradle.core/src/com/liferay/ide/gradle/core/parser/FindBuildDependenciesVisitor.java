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
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;

/**
 * @author Terry Jia
 */
public class FindBuildDependenciesVisitor extends CodeVisitorSupport {

	public List<GradleDependency> getDependencies() {
		return _dependencies;
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		List<MapEntryExpression> mapEntryExpressions = expression.getMapEntryExpressions();
		Map<String, String> dependenceMap = new HashMap<>();

		for (MapEntryExpression mapEntryExpression : mapEntryExpressions) {
			Expression key = mapEntryExpression.getKeyExpression();
			Expression value = mapEntryExpression.getValueExpression();

			dependenceMap.put(key.getText(), value.getText());
		}

		_dependencies.add(new GradleDependency(dependenceMap));

		super.visitMapExpression(expression);
	}

	private List<GradleDependency> _dependencies = new ArrayList<>();

}