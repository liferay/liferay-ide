/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.PortletModePossibleValueService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@Label( standard = "Supports configuration" )
@GenerateImpl
@Image( path = "images/obj16/supports_obj.gif" )
public interface ISupports extends IModelElement {

	ModelElementType TYPE = new ModelElementType( ISupports.class );

	// *** MimeType ***

	@Label( standard = "Mime Type" )
	@DefaultValue( text = "text/html" )
	@Required
	@XmlBinding( path = "mime-type" )
	ValueProperty PROP_MIME_TYPE = new ValueProperty( TYPE, "MimeType" );

	Value<String> getMimeType();

	void setMimeType( String value );

	// *** PortletModes ***

	@Type( base = IPortletMode.class )
	@Label( standard = "Portlet Modes" )
	@Service( impl = PortletModePossibleValueService.class )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet-mode", type = IPortletMode.class ) )
	ListProperty PROP_PORTLET_MODES = new ListProperty( TYPE, "PortletModes" );

	ModelElementList<IPortletMode> getPortletModes();

	// *** Window States ***

	@Type( base = IWindowState.class )
	@Label( standard = "Window States" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "window-state", type = IWindowState.class ) )
	ListProperty PROP_WINDOW_STATES = new ListProperty( TYPE, "WindowStates" );

	ModelElementList<IWindowState> getWindowStates();
}
