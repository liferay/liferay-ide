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
 *******************************************************************************/

package com.liferay.ide.taglib.ui.model;

import com.liferay.ide.taglib.ui.model.internal.PreviewSourceContentProvider;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@XmlBinding( path = "tag" )
public interface Tag extends Element
{

    ElementType TYPE = new ElementType( Tag.class );

    // *** Name ***

    @XmlBinding( path = "name" )
    @ReadOnly
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" ); //$NON-NLS-1$

    Value<String> getName();

    // *** Prefix ***

    @XmlBinding( path = "prefix" )
    @ReadOnly
    ValueProperty PROP_PREFIX = new ValueProperty( TYPE, "Prefix" ); //$NON-NLS-1$

    Value<String> getPrefix();

    // *** Required Attributes ***

    @Type( base = Attribute.class )
    @ReadOnly
    @XmlListBinding
    (
        path = "required",
        mappings = @XmlListBinding.Mapping
        (
            element = "attribute",
            type = Attribute.class
        )
    )
    ListProperty PROP_REQUIRED_ATTRIBUTES = new ListProperty( TYPE, "RequiredAttributes" ); //$NON-NLS-1$

    ElementList<Attribute> getRequiredAttributes();

    // *** Event Attributes ***

    @Type( base = Attribute.class )
    @ReadOnly
    @XmlListBinding
    (
        path = "events",
        mappings = @XmlListBinding.Mapping
        (
            element = "attribute",
            type = Attribute.class
        )
    )
    ListProperty PROP_EVENTS = new ListProperty( TYPE, "Events" ); //$NON-NLS-1$

    ElementList<Attribute> getEvents();

    // *** Other Attributes ***

    @Type( base = Attribute.class )
    @ReadOnly
    @XmlListBinding( path = "other", mappings = @XmlListBinding.Mapping( element = "attribute", type = Attribute.class ) )
    ListProperty PROP_OTHER_ATTRIBUTES = new ListProperty( TYPE, "OtherAttributes" ); //$NON-NLS-1$

    ElementList<Attribute> getOtherAttributes();

    @Service( impl = PreviewSourceContentProvider.class )
    @DependsOn( { "RequiredAttributes/*", "Events/*", "OtherAttributes/*" } )
    @ReadOnly
    ValueProperty PROP_PREVIEW = new ValueProperty( TYPE, "Preview" ); //$NON-NLS-1$

    Value<String> getPreview();

    @Service( impl = PreviewSourceContentProvider.class )
    @DependsOn( { "RequiredAttributes/*", "Events/*", "OtherAttributes/*" } )
    @ReadOnly
    ValueProperty PROP_SOURCE = new ValueProperty( TYPE, "Source" ); //$NON-NLS-1$

    Value<String> getSource();
}
