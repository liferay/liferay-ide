/*******************************************************************************
 *  Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *
 *   Contributors:
 *          Gregory Amerson -initial implementation
 *******************************************************************************/

package com.liferay.ide.properties.model.pluginpackage;

import com.liferay.ide.properties.model.annotations.PropertyBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author Gregory Amerson
 */
public interface PluginPackageProperties extends Element
{
    ElementType TYPE = new ElementType( PluginPackageProperties.class );

    // *** Author ***

    @Label( standard = "&Author" )
    @PropertyBinding( key = "author" )
    ValueProperty PROP_AUTHOR = new ValueProperty( TYPE, "Author" ); //$NON-NLS-1$

    Value<String> getAuthor();

    void setAuthor( String value );

    // *** ChangeLog ***

    @Label( standard = "&Change Log" )
    @PropertyBinding( key = "change-log" )
    ValueProperty PROP_CHANGE_LOG = new ValueProperty( TYPE, "ChangeLog" ); //$NON-NLS-1$

    Value<String> getChangeLog();

    void setChangeLog( String value );

    // *** ChangeLog ***

    @Label( standard = "Short &Description" )
    @PropertyBinding( key = "short-description" )
    ValueProperty PROP_SHORT_DESCRIPTION = new ValueProperty( TYPE, "ShortDescription" ); //$NON-NLS-1$

    Value<String> getShortDescription();

    void setShortDescription( String value );

    @Type( base = Boolean.class )
    @Label( standard = "Speed &Filters" )
    @PropertyBinding( key = "speed-filters-enabled" )
    ValueProperty PROP_SPEED_FILTERS = new ValueProperty( TYPE, "SpeedFilters" ); //$NON-NLS-1$

    Value<Boolean> getSpeedFilters();

    void setSpeedFilters( String value );

    void setSpeedFilters( Boolean value );

}
