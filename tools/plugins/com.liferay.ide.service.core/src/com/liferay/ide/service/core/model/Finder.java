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
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/finder_16x16.gif")
public interface Finder extends Element
{
    ElementType TYPE = new ElementType( Finder.class );

    // *** Name ***

    @XmlBinding( path = "@name" )
    @Label( standard = "&name" )
    @Required
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String value );

    // *** Return Type ***

    @XmlBinding( path = "@return-type" )
    @Label( standard = "&return type" )
    @Required
    ValueProperty PROP_RETURN_TYPE = new ValueProperty( TYPE, "ReturnType" ); //$NON-NLS-1$

    Value<String> getReturnType();

    void setReturnType( String value );

    // *** Unique ***

    @Type( base = Boolean.class )
    @Label( standard = "&unique" )
    @XmlBinding( path = "@unique" )
    @DefaultValue( text = "false" )
    ValueProperty PROP_UNIQUE = new ValueProperty( TYPE, "Unique" ); //$NON-NLS-1$

    Value<Boolean> isUnique();

    void setUnique( String value );

    void setUnique( Boolean value );

    // *** Where ***

    @XmlBinding( path = "@where" )
    @Label( standard = "&where" )
    ValueProperty PROP_WHERE = new ValueProperty( TYPE, "Where" ); //$NON-NLS-1$

    Value<String> getWhere();

    void setWhere( String value );

    // *** DB Index ***

    @Type( base = Boolean.class )
    @Label( standard = "&db index" )
    @XmlBinding( path = "@db-index" )
    @DefaultValue( text = "true" )
    ValueProperty PROP_DB_INDEX = new ValueProperty( TYPE, "DbIndex" ); //$NON-NLS-1$

    Value<Boolean> isDbIndex();

    void setDbIndex( String value );

    void setDbIndex( Boolean value );

    // *** Finder Columns ***

    @Type( base = FinderColumn.class )
    @Label( standard = "finder columns" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "finder-column", type = FinderColumn.class ) )
    @CountConstraint( min = 1 )
    ListProperty PROP_FINDER_COLUMNS = new ListProperty( TYPE, "FinderColumns" ); //$NON-NLS-1$

    ElementList<FinderColumn> getFinderColumns();

}
