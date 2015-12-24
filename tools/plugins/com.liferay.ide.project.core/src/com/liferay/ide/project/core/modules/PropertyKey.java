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
package com.liferay.ide.project.core.modules;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;


/**
 * @author Simon Jiang
 */
public interface PropertyKey extends Element
{
    ElementType TYPE = new ElementType( PropertyKey.class );
    
    // *** Name ***
   
    @Label( standard = "name" )
    @Required
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    void setName( String value );
    Value<String> getName( );
    
    // *** Value ***
    
    @Label( standard = "value" )
    @Required
    ValueProperty PROP_Value = new ValueProperty( TYPE, "Value" );
    
    void setValue( String value );
    Value<String> getValue( );
}
