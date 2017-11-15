/**
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
 */

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.PortletModePossibleValueService;
import com.liferay.ide.portlet.core.model.internal.WindowStatesPossibleValueService;

import org.eclipse.sapphire.Collation;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.InitialValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/obj16/supports_obj.gif")
@Label(standard = "Supports configuration")
public interface Supports extends Element {

	public ElementType TYPE = new ElementType(Supports.class);

	// *** MimeType ***

	@Label(standard = "Mime Type")
	@InitialValue(text = "text/html")
	@Required
	@XmlBinding(path = "mime-type")
	public ValueProperty PROP_MIME_TYPE = new ValueProperty(TYPE, "MimeType");

	public Value<String> getMimeType();

	public void setMimeType(String value);

	// *** PortletModes ***

	@Type(base = PortletMode.class)
	@Label(standard = "Portlet Modes")
	@Service(impl = PortletModePossibleValueService.class)
	@Collation(ignoreCaseDifferences = "true")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "portlet-mode", type = PortletMode.class))
	public ListProperty PROP_PORTLET_MODES = new ListProperty(TYPE, "PortletModes");

	public ElementList<PortletMode> getPortletModes();

	// *** Window States ***

	@Type(base = WindowState.class)
	@Label(standard = "Window States")
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "window-state", type = WindowState.class))
	@Service(impl = WindowStatesPossibleValueService.class)
	@Collation(ignoreCaseDifferences = "true")
	public 	ListProperty PROP_WINDOW_STATES = new ListProperty(TYPE, "WindowStates");

	public ElementList<WindowState> getWindowStates();
}