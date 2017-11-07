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

package com.liferay.ide.project.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;

/**
 * @author Simon Jiang
 */
public interface NamedItem extends Element {

	public ElementType TYPE = new ElementType(NamedItem.class);

	public Value<String> getExtDesc();

	public Value<String> getName();

	public void setExtDesc(String value);

	public void setName(String value);

	public ValueProperty PROP_EXT_DESC = new ValueProperty(TYPE, "ExtDesc");

	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

}