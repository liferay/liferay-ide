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
@Image(path = "images/finder_column_16x16.png")
public interface FinderColumn extends IModelElement 
{

    ModelElementType TYPE = new ModelElementType( FinderColumn.class );
    
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

	// ** Comparator ***

	@Label(standard = "comparator")
	@XmlBinding(path = "@comparator")
	@PossibleValues(values = { "=", "!=", "<", "<=", ">", ">=", "LIKE" }, invalidValueMessage = "{0} is not a valid comparator.")
	ValueProperty PROP_COMPARATOR = new ValueProperty(TYPE, "Comparator"); //$NON-NLS-1$

	Value<String> getComparator();

	void setComparator(String value);

	// ** Arrayable Operator ***

	@Label(standard = "arrayable operator")
	@XmlBinding(path = "@arrayable-operator")
	@PossibleValues(values = { "AND", "OR" }, invalidValueMessage = "{0} is not a valid arryable operator.")
	ValueProperty PROP_ARRAYABLE_OPERATOR = new ValueProperty(TYPE, "ArrayableOperator"); //$NON-NLS-1$

	Value<String> getArrayableOperator();

	void setArrayableOperator(String value);

}
