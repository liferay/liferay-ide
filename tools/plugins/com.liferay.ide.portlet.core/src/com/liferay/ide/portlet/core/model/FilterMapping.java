/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.FilterReferenceService;
import com.liferay.ide.portlet.core.model.internal.PortletReferenceService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

/**
 * @author Kamesh Sampath
 */
@Label( standard = "filter mapping" )
@Image( path = "images/elcl16/filter_mapping_16x16.gif" )
public interface FilterMapping extends Element
{

    ElementType TYPE = new ElementType( FilterMapping.class );

    // *** IFilter ***

    @Reference( target = Filter.class )
    @Service( impl = FilterReferenceService.class )
    @Label( standard = "filter" )
    @Required
    @PossibleValues( property = "/Filters/Name" )
    @XmlBinding( path = "filter-name" )
    ValueProperty PROP_FILTER = new ValueProperty( TYPE, "Filter" ); //$NON-NLS-1$

    ReferenceValue<String, Filter> getFilter();

    void setFilter( String value );

    // *** Portlet ***

    @Reference( target = Portlet.class )
    @Service( impl = PortletReferenceService.class )
    @Label( standard = "portlet" )
    @Required
    @PossibleValues( property = "/Portlets/PortletName" )
    @XmlValueBinding( path = "portlet-name", removeNodeOnSetIfNull = false )
    ValueProperty PROP_PORTLET = new ValueProperty( TYPE, "Portlet" ); //$NON-NLS-1$

    ReferenceValue<String, Portlet> getPortlet();

    void setPortlet( String value );

}
