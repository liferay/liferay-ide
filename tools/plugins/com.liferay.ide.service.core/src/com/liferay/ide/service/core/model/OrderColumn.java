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
@Image(path = "images/column_16x16.gif")
public interface OrderColumn extends Element {

	public ElementType TYPE = new ElementType(OrderColumn.class);

	public Value<String> getName();

	// *** Name ***

	public Value<String> getOrderBy();

	public Value<Boolean> isCaseSensitive();

	public void setCaseSensitive(Boolean value);

	// *** CaseSensitive ***

	public void setCaseSensitive(String value);

	public void setName(String value);

	public void setOrderBy(String value);

	@Label(standard = "&case sensitive")
	@Type(base = Boolean.class)
	@XmlBinding(path = "@case-sensitive")
	public ValueProperty PROP_CASE_SENSITIVE = new ValueProperty(TYPE, "CaseSensitive");

	// *** Order By ***

	@Label(standard = "&name")
	@Required
	@XmlBinding(path = "@name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "order by")
	@PossibleValues(invalidValueMessage = "{0} is not valid.", values = {"asc", "desc"})
	@XmlBinding(path = "@order-by")
	public ValueProperty PROP_ORDER_BY = new ValueProperty(TYPE, "OrderBy");

}