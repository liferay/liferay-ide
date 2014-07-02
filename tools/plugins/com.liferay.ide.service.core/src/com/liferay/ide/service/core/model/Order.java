/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Gregory Amerson
 */
public interface Order extends Element
{

    ElementType TYPE = new ElementType( Order.class );

    // *** By ***

    @Label( standard = "by" )
    @XmlBinding( path = "@by" )
    @PossibleValues( values = { "asc", "desc" }, invalidValueMessage = "{0} is not valid." )
    ValueProperty PROP_BY = new ValueProperty( TYPE, "By" ); //$NON-NLS-1$

    Value<String> getBy();

    void setBy( String value );

    // *** Order Columns ***

    @Type( base = OrderColumn.class )
    @Label( standard = "order columns" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "order-column", type = OrderColumn.class ) )
    @CountConstraint( min = 1 )
    ListProperty PROP_ORDER_COLUMNS = new ListProperty( TYPE, "OrderColumns" ); //$NON-NLS-1$

    ElementList<OrderColumn> getOrderColumns();

}
