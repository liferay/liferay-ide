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

package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.layouttpl.core.model.internal.PortletColumnFullWeightDefaultValueService;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnWeightDefaultValueService;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnWeightValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Kuo Zhang
 */
@Label( standard = "Column" )
public interface PortletColumnElement extends CanAddPortletLayouts
{
    ElementType TYPE = new ElementType( PortletColumnElement.class );

    // *** NumId ***

    ValueProperty PROP_NUM_ID = new ValueProperty( TYPE, "NumId" );

    Value<String> getNumId();
    void setNumId( String value );

    // *** Class Name ***

    @DefaultValue( text = "portlet-column" )
    ValueProperty PROP_ClASS_NAME = new ValueProperty( TYPE, "ClassName" );

    Value<String> getClassName();
    void setClassName( String value );

    // *** Weight ***

    @Type( base = Integer.class )
    @Services
    ( 
        value = 
        { 
            @Service( impl = PortletColumnWeightDefaultValueService.class ) ,
            @Service( impl = PortletColumnWeightValidationService.class )
        }
    )
    @Required
    ValueProperty PROP_WEIGHT = new ValueProperty( TYPE, "Weight" );

    Value<Integer> getWeight();
    void setWeight( String value );
    void setWeight( Integer value );


    // *** Full Weight ***

    @Type( base = Integer.class )
    @Service( impl = PortletColumnFullWeightDefaultValueService.class )
    ValueProperty PROP_FULL_WEIGHT = new ValueProperty( TYPE, "FullWeight" );

    Value<Integer> getFullWeight();
    void setFullWeight( String value );
    void setFullWeight( Integer value );

    // *** Is First ***

    @Required
    @Type( base = Boolean.class )
    ValueProperty PROP_FIRST = new ValueProperty( TYPE, "First" );

    Value<Boolean> getFirst();
    void setFirst( Boolean value );
    void setFirst( String value );

    // *** Is Last ***

    @Required
    @DefaultValue( text = "false" )
    @Type( base = Boolean.class )
    ValueProperty PROP_LAST = new ValueProperty( TYPE, "Last" );

    Value<Boolean> getLast();
    void setLast( Boolean value );
    void setLast( String value );

    // *** Is Only ***

    @Required
    @DefaultValue( text = "false" )
    @Type( base = Boolean.class )
    ValueProperty PROP_ONLY = new ValueProperty( TYPE, "Only" );

    Value<Boolean> getOnly();
    void setOnly( Boolean value );
    void setOnly( String value );

    // *** Column Descriptor ***

    @DefaultValue( text = "" )
    ValueProperty PROP_COLUMN_DESCRIPTOR = new ValueProperty( TYPE, "ColumnDescriptor" );

    Value<String> getColumnDescriptor();
    void setColumnDescriptor( String value );

    // *** Column Content Descriptor ***

    @DefaultValue( text = "" )
    ValueProperty PROP_COLUMN_CONTENT_DESCRIPTOR = new ValueProperty( TYPE, "ColumnContentDescriptor" );

    Value<String> getColumnContentDescriptor();
    void setColumnContentDescriptor( String value );

}
