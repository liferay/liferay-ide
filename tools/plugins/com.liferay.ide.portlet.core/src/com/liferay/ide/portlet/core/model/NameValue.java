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

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface NameValue extends Element {

	public ElementType TYPE = new ElementType(NameValue.class);

	/**
	 * Name Element
	 */
	@Label(standard = "Name")
	@Required
	@Unique
	@XmlBinding(path = "name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	public Value<String> getName();

	public void setName(String name);

	/**
	 * Value Element
	 */
	@Label(standard = "Value")
	@XmlBinding(path = "value")
	public 	ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");

	Value<String> getValue();

	void setValue(String value);
}