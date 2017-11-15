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

package com.liferay.ide.layouttpl.core.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Required;

/**
 * @author Kuo Zhang
 * @author Joye Luo
 */
public interface LayoutTplElement extends CanAddPortletLayouts {

	public static final ElementType TYPE = new ElementType(LayoutTplElement.class);

	@DefaultValue(text = "true")
	@Required
	@Type(base = Boolean.class)
	public static final ValueProperty PROP_BOOTSTRAP_STYLE = new ValueProperty(TYPE, "BootstrapStyle");

	@DefaultValue(text = "main-content")
	public static final ValueProperty PROP_ID = new ValueProperty(TYPE, "Id");

	@Required
	@Type(base = Boolean.class)
	public static final ValueProperty PROP_IS_62 = new ValueProperty(TYPE, "Is62");

	@DefaultValue(text = "main")
	public static final ValueProperty PROP_ROLE = new ValueProperty(TYPE, "Role");

	@Required
	public static final ValueProperty PROP_ClASS_NAME = new ValueProperty(TYPE, "ClassName");

	public Value<Boolean> getBootstrapStyle();

	public Value<String> getClassName();

	public Value<String> getId();

	public Value<Boolean> getIs62();

	public Value<String> getRole();

	public void setBootstrapStyle(Boolean value);

	public void setBootstrapStyle(String value);

	public void setClassName(String className);

	public void setId(String id);

	public void setIs62(Boolean value);

	public void setIs62(String value);

	public void setRole(String role);

}