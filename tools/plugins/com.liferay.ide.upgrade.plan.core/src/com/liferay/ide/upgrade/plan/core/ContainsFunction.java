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

package com.liferay.ide.upgrade.plan.core;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.sapphire.modeling.el.AggregateFunction;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;

/**
 * @author Simon Jiang
 */
public class ContainsFunction extends AggregateFunction {

	@Override
	public FunctionResult evaluate(FunctionContext context) {
		return new AggregateFunctionResult(this, context) {

			@Override
			protected Object evaluate(List<Object> items) {
				Object targetValue = operand(2);

				Stream<Object> itemStream = items.stream();

				return itemStream.map(
					item -> cast(item, String.class)
				).filter(
					item -> item.equals(targetValue)
				).findAny(
				).isPresent();
			}

		};
	}

	@Override
	public String name() {
		return "Contains";
	}

}