/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/
package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Gregory Amerson
 */
@GenerateImpl
public interface Order extends IModelElement 
{

    ModelElementType TYPE = new ModelElementType( Order.class );
    
	// *** By ***

	@Label(standard = "by")
	@XmlBinding(path = "@by")
	@PossibleValues(values = { "asc", "desc" }, invalidValueMessage = "{0} is not valid.")
	ValueProperty PROP_BY = new ValueProperty(TYPE, "By"); //$NON-NLS-1$

	Value<String> getBy();

	void setBy(String value);

	// *** Order Columns ***
    
	@Type(base = OrderColumn.class)
	@Label(standard = "order columns")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "order-column", type = OrderColumn.class))
	@CountConstraint(min = 1)
	ListProperty PROP_ORDER_COLUMNS = new ListProperty(TYPE, "OrderColumns"); //$NON-NLS-1$

	ModelElementList<OrderColumn> getOrderColumns();

}
