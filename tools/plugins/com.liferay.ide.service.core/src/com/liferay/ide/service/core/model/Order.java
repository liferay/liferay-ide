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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface Order extends Element {

	public ElementType TYPE = new ElementType(Order.class);

	public Value<String> getBy();

	// *** By ***

	public ElementList<OrderColumn> getOrderColumns();

	public void setBy(String value);

	@Label(standard = "by")
	@PossibleValues(invalidValueMessage = "{0} is not valid.", values = {"asc", "desc"})
	@XmlBinding(path = "@by")
	public ValueProperty PROP_BY = new ValueProperty(TYPE, "By");

	// *** Order Columns ***

	@Label(standard = "order columns")
	@Length(min = 1)
	@Type(base = OrderColumn.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "order-column", type = OrderColumn.class))
	public ListProperty PROP_ORDER_COLUMNS = new ListProperty(TYPE, "OrderColumns");

}