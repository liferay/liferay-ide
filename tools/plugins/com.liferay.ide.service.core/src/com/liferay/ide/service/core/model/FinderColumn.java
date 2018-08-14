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

package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/finder_column_16x16.png")
public interface FinderColumn extends Element {

	public ElementType TYPE = new ElementType(FinderColumn.class);

	public Value<String> getArrayableOperator();

	// *** Name ***

	public Value<String> getComparator();

	public Value<String> getName();

	public Value<Boolean> isCaseSensitive();

	// *** CaseSensitive ***

	public void setArrayableOperator(String value);

	public void setCaseSensitive(Boolean value);

	public void setCaseSensitive(String value);

	public void setComparator(String value);

	// ** Comparator ***

	public void setName(String value);

	@Label(standard = "arrayable operator")
	@PossibleValues(invalidValueMessage = "{0} is not a valid arryable operator.", values = {"AND", "OR"})
	@XmlBinding(path = "@arrayable-operator")
	public ValueProperty PROP_ARRAYABLE_OPERATOR = new ValueProperty(TYPE, "ArrayableOperator");

	@Label(standard = "&case sensitive")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@case-sensitive")
	public ValueProperty PROP_CASE_SENSITIVE = new ValueProperty(TYPE, "CaseSensitive");

	// ** Arrayable Operator ***

	@Label(standard = "comparator")
	@PossibleValues(
		invalidValueMessage = "{0} is not a valid comparator.", values = {"=", "!=", "<", "<=", ">", ">=", "LIKE"}
	)
	@XmlBinding(path = "@comparator")
	public ValueProperty PROP_COMPARATOR = new ValueProperty(TYPE, "Comparator");

	@Label(standard = "&name")
	@Required
	@XmlBinding(path = "@name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

}