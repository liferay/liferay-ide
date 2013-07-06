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

package com.liferay.ide.portlet.core.display.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image( path = "images/elcl16/category_16x16.gif" )
public interface Category extends Element
{
    ElementType TYPE = new ElementType( Category.class );

    // *** Name ***

    @Label( standard = "Name" )
    @Required
    @NoDuplicates
    @XmlBinding( path = "@name" )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    void setName( String name );

    // *** Categories ***

    @Type( base = Category.class )
    @Label( standard = "Categories" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "category", type = Category.class ) } )
    ListProperty PROP_CATEGORIES = new ListProperty( TYPE, "Categories" ); //$NON-NLS-1$

    ElementList<Category> getCategories();

    // *** Portlets ***

    @Type( base = DisplayPortlet.class )
    @Label( standard = "Portlets" )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "portlet", type = DisplayPortlet.class ) } )
    ListProperty PROP_PORTLETS = new ListProperty( TYPE, "Portlets" ); //$NON-NLS-1$

    ElementList<DisplayPortlet> getPortlets();

}
