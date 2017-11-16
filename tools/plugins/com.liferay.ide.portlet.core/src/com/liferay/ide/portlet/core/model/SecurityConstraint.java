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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/constraint_16x16.png")
public interface SecurityConstraint extends Element, Identifiable, Displayable {

	public ElementType TYPE = new ElementType(SecurityConstraint.class);

	// *** Portlet Name ***

	@Type(base = PortletName.class)
	@Label(standard = "Portlet name")
	@Required
	@Length(min = 1)
	@Unique
	@XmlListBinding(path = "portlet-collection", mappings = @XmlListBinding.Mapping(element = "portlet-name", type = PortletName.class))
	public ListProperty PROP_PORTLET_NAMES = new ListProperty(TYPE, "PortletNames");

	public 	ElementList<PortletName> getPortletNames();

	// *** UserDataConstraint ***

	@Type(base = UserDataConstraint.class)
	@Label(standard = "User Data Constraint")
	@Required
	@XmlBinding(path = "user-data-constraint")
	public ImpliedElementProperty PROP_USER_DATA_CONSTRAINT = new ImpliedElementProperty(TYPE, "UserDataConstraint");

	public UserDataConstraint getUserDataConstraint();

}