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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.RoleEditModeBinding;
import com.liferay.ide.kaleo.core.model.internal.RoleNamePossibleValuesMetaService;
import com.liferay.ide.kaleo.core.model.internal.RoleNamePossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Service.Context;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public interface Role extends Element {

	public ElementType TYPE = new ElementType(Role.class);

	public Value<Boolean> getAutoCreate();

	public Value<RoleEditMode> getEditMode();

	public Value<String> getName();

	public Value<Integer> getRoleId();

	public Value<RoleType> getRoleType();

	public void setAutoCreate(Boolean value);

	public void setAutoCreate(String value);

	public void setEditMode(RoleEditMode editMode);

	public void setEditMode(String editMode);

	public void setName(String value);

	public void setRoleId(Integer val);

	public void setRoleId(String val);

	public void setRoleType(RoleType roleType);

	public void setRoleType(String roleType);

	@Label(standard = "&auto-create")
	@Type(base = Boolean.class)
	@XmlBinding(path = "auto-create")
	public ValueProperty PROP_AUTO_CREATE = new ValueProperty(TYPE, "AutoCreate");

	@CustomXmlValueBinding(impl = RoleEditModeBinding.class)
	@Label(standard = "edit mode")
	@Type(base = RoleEditMode.class)
	public ValueProperty PROP_EDIT_MODE = new ValueProperty(TYPE, "EditMode");

	@Label(standard = "&name")
	@Required
	@Services(
		value = {
			@Service(context = Context.METAMODEL, impl = RoleNamePossibleValuesMetaService.class),
			@Service(impl = RoleNamePossibleValuesService.class)
		}
	)
	@XmlBinding(path = "name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "&role-id")
	@Type(base = Integer.class)
	@XmlBinding(path = "role-id")
	public ValueProperty PROP_ROLE_ID = new ValueProperty(TYPE, "RoleId");

	@DefaultValue(text = "regular")
	@Label(standard = "role type")
	@Type(base = RoleType.class)
	@XmlBinding(path = "role-type")
	public ValueProperty PROP_ROLE_TYPE = new ValueProperty(TYPE, "RoleType");

}