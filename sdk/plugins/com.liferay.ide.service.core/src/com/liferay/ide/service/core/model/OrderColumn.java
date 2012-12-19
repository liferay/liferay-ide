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
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Gregory Amerson
 */
@GenerateImpl
@Image(path = "images/column_16x16.gif")
public interface OrderColumn extends IModelElement 
{
    ModelElementType TYPE = new ModelElementType( OrderColumn.class );
    
	// *** Name ***
    
    @XmlBinding(path = "@name")
	@Label(standard = "&name")
    @Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name"); //$NON-NLS-1$

    Value<String> getName();

	void setName(String value);

	// *** CaseSensitive ***

	@Type(base = Boolean.class)
	@Label(standard = "&case sensitive")
	@XmlBinding(path = "@case-sensitive")
	ValueProperty PROP_CASE_SENSITIVE = new ValueProperty(TYPE, "CaseSensitive"); //$NON-NLS-1$

	Value<Boolean> isCaseSensitive();

	void setCaseSensitive(String value);

	void setCaseSensitive(Boolean value);

	// *** Order By ***

	@Label(standard = "order by")
	@XmlBinding(path = "@order-by")
	@PossibleValues(values = { "asc", "desc" }, invalidValueMessage = "{0} is not valid.")
	ValueProperty PROP_ORDER_BY = new ValueProperty(TYPE, "OrderBy"); //$NON-NLS-1$

	Value<String> getOrderBy();

	void setOrderBy(String value);

}
