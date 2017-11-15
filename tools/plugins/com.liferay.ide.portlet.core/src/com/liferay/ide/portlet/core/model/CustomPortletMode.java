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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface CustomPortletMode extends Element, Describeable, Identifiable {

	public ElementType TYPE = new ElementType(CustomPortletMode.class);

	// *** PortletMode ***

	@Required
	@Unique
	@Label(standard = "Portlet Mode")
	@XmlBinding(path = "portlet-mode")
	public ValueProperty PROP_PORTLET_MODE = new ValueProperty(TYPE, "PortletMode");

	public Value<String> getPortletMode();

	public void setPortletMode(String value);

	// void setPortletMode( IPortletMode value );

	/**
	 * Portlet Managed
	 */
	@Type(base = Boolean.class)
	@Label(standard = "Portal managed")
	@DefaultValue(text = "true")
	@XmlBinding(path = "portal-managed")
	public ValueProperty PROP_PORTAL_MANAGED = new ValueProperty(TYPE, "PortalManaged");

	public Value<Boolean> getPortalManaged();

	public void setPortalManaged(String value);

	public void setPortalManaged(Boolean value);
}