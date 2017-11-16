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

package com.liferay.ide.portlet.core.model.lfrportlet.common;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface ILiferayPortletAppBase extends Element {

	public ElementType TYPE = new ElementType(ILiferayPortletAppBase.class);

	// *** RoleMappers ***

	@Type(base = IRoleMapper.class)
	@Label(standard = "Role Mappers")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "role-mapper", type = IRoleMapper.class)})
	public ListProperty PROP_ROLE_MAPPERS = new ListProperty(TYPE, "RoleMappers");

	public ElementList<IRoleMapper> getRoleMappers();

	// *** CustomUserAttributes ***

	@Type(base = ICustomUserAttribute.class)
	@Label(standard = "Custom User Attribute")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "custom-user-attribute", type = ICustomUserAttribute.class)})
	public ListProperty PROP_CUSTOM_USER_ATTRIBUTES = new ListProperty(TYPE, "CustomUserAttributes");

	public ElementList<ICustomUserAttribute> getCustomUserAttributes();

}