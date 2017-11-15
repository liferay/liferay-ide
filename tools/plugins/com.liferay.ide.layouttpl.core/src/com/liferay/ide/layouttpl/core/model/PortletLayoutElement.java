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

import com.liferay.ide.layouttpl.core.model.internal.PortletColumnsListener;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnsValidtionSerivce;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Kuo Zhang
 */
@Label(standard = "Row")
public interface PortletLayoutElement extends Element {

	public static final ElementType TYPE = new ElementType(PortletLayoutElement.class);

	@Listeners(PortletColumnsListener.class)
	@Service(impl = PortletColumnsValidtionSerivce.class)
	@Type(base = PortletColumnElement.class)
	public static final ListProperty PROP_PORTLET_COLUMNS = new ListProperty(TYPE, "PortletColumns");

	@DefaultValue(
		text = "${ Concat( \"portlet-layout\", Root.BootstrapStyle ? (Root.Is62 ? \" row-fluid\" : \" row\") : \"\" ) }"
	)
	@Required
	public static final ValueProperty PROP_ClASS_NAME = new ValueProperty(TYPE, "ClassName");

	public Value<String> getClassName();

	public ElementList<PortletColumnElement> getPortletColumns();

	public void setClassName(String className);

}