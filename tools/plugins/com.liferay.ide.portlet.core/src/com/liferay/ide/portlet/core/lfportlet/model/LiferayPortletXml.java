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

package com.liferay.ide.portlet.core.lfportlet.model;

import com.liferay.ide.portlet.core.model.SecurityRoleRef;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Simon Jiang
 */
@CustomXmlRootBinding(LiferayPortletRootElementController.class)
@Image(path = "images/eview16/portlet_app_hi.gif")
@XmlBinding(path = "liferay-portlet-app")
public interface LiferayPortletXml extends Element {

	public ElementType TYPE = new ElementType(LiferayPortletXml.class);

	public ElementList<CustomUserAttribute> getCustomUserAttributes();

	public ElementList<LiferayPortlet> getPortlets();

	public ElementList<SecurityRoleRef> getRoleMappers();

	@Type(base = CustomUserAttribute.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "custom-user-attribute", type = CustomUserAttribute.class)
	)
	public ListProperty PROP_CUSTOM_USER_ATTRIBUTES = new ListProperty(TYPE, "CustomUserAttributes");

	@Type(base = LiferayPortlet.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "portlet", type = LiferayPortlet.class))
	public ListProperty PROP_PORTLETS = new ListProperty(TYPE, "Portlets");

	@Type(base = SecurityRoleRef.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "role-mapper", type = SecurityRoleRef.class))
	public ListProperty PROP_ROLE_MAPPERS = new ListProperty(TYPE, "RoleMappers");

}