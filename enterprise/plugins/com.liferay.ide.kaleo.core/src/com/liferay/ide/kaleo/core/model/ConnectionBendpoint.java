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

package com.liferay.ide.kaleo.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author Shenxue Zhou
 */
public interface ConnectionBendpoint extends Element {

	public ElementType TYPE = new ElementType(ConnectionBendpoint.class);

	public Value<Integer> getX();

	public Value<Integer> getY();

	public void setX(Integer value);

	public void setX(String value);

	public void setY(Integer value);

	public void setY(String value);

	@DefaultValue(text = "0")
	@Type(base = Integer.class)
	public ValueProperty PROP_X = new ValueProperty(TYPE, "X");

	@DefaultValue(text = "0")
	@Type(base = Integer.class)
	public ValueProperty PROP_Y = new ValueProperty(TYPE, "Y");

}