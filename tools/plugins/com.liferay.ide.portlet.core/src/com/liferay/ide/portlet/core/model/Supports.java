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

import com.liferay.ide.portlet.core.model.internal.PortletModePossibleValueService;
import com.liferay.ide.portlet.core.model.internal.WindowStatesPossibleValueService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.InitialValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Label( standard = "Supports configuration" )
@Image( path = "images/obj16/supports_obj.gif" )
public interface Supports extends Element
{

    ElementType TYPE = new ElementType( Supports.class );

    // *** MimeType ***

    @Label( standard = "Mime Type" )
    @InitialValue( text = "text/html" )
    @Required
    @XmlBinding( path = "mime-type" )
    ValueProperty PROP_MIME_TYPE = new ValueProperty( TYPE, "MimeType" ); //$NON-NLS-1$

    Value<String> getMimeType();

    void setMimeType( String value );

    // *** PortletModes ***

    @Type( base = PortletMode.class )
    @Label( standard = "Portlet Modes" )
    @Service( impl = PortletModePossibleValueService.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet-mode", type = PortletMode.class ) )
    ListProperty PROP_PORTLET_MODES = new ListProperty( TYPE, "PortletModes" ); //$NON-NLS-1$

    ElementList<PortletMode> getPortletModes();

    // *** Window States ***

    @Type( base = WindowState.class )
    @Label( standard = "Window States" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "window-state", type = WindowState.class ) )
    @Service( impl = WindowStatesPossibleValueService.class )
    ListProperty PROP_WINDOW_STATES = new ListProperty( TYPE, "WindowStates" ); //$NON-NLS-1$

    ElementList<WindowState> getWindowStates();
}
