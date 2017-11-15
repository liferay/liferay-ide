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

import com.liferay.ide.layouttpl.core.model.internal.PortletColumnWeightInitialValueService;
import com.liferay.ide.layouttpl.core.model.internal.PortletColumnWeightValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;

/**
 * @author Kuo Zhang
 */
@Label(standard = "Column")
public interface PortletColumnElement extends CanAddPortletLayouts {

	public static final ElementType TYPE = new ElementType(PortletColumnElement.class);

	@DefaultValue(text = "")
	public static final ValueProperty PROP_COLUMN_CONTENT_DESCRIPTOR = new ValueProperty(
		TYPE, "ColumnContentDescriptor");

	@DefaultValue(text = "")
	public static final ValueProperty PROP_COLUMN_DESCRIPTOR = new ValueProperty(TYPE, "ColumnDescriptor");

	@Required
	@Type(base = Boolean.class)
	public static final ValueProperty PROP_FIRST = new ValueProperty(TYPE, "First");

	@DefaultValue(text = "${Root.BootstrapStyle ? \"12\" : \"100\"}")
	@Type(base = Integer.class)
	public static final ValueProperty PROP_FULL_WEIGHT = new ValueProperty(TYPE, "FullWeight");

	@DefaultValue(text = "false")
	@Required
	@Type(base = Boolean.class)
	public static final ValueProperty PROP_LAST = new ValueProperty(TYPE, "Last");

	public static final ValueProperty PROP_NUM_ID = new ValueProperty(TYPE, "NumId");

	@DefaultValue(text = "false")
	@Required
	@Type(base = Boolean.class)
	public static final ValueProperty PROP_ONLY = new ValueProperty(TYPE, "Only");

	@DefaultValue(text = "${Root.BootstrapStyle ? \"3\" : \"25\"}")
	@Required
	@Services(
		{
			@Service(impl = PortletColumnWeightValidationService.class),
			@Service(impl = PortletColumnWeightInitialValueService.class)
		}
	)
	@Type(base = Integer.class)
	public static final ValueProperty PROP_WEIGHT = new ValueProperty(TYPE, "Weight");

	@DefaultValue(text = "portlet-column")
	public static final ValueProperty PROP_ClASS_NAME = new ValueProperty(TYPE, "ClassName");

	public Value<String> getClassName();

	public Value<String> getColumnContentDescriptor();

	public Value<String> getColumnDescriptor();

	public Value<Boolean> getFirst();

	public Value<Integer> getFullWeight();

	public Value<Boolean> getLast();

	public Value<String> getNumId();

	public Value<Boolean> getOnly();

	public Value<Integer> getWeight();

	public void setClassName(String value);

	public void setColumnContentDescriptor(String value);

	public void setColumnDescriptor(String value);

	public void setFirst(Boolean value);

	public void setFirst(String value);

	public void setFullWeight(Integer value);

	public void setFullWeight(String value);

	public void setLast(Boolean value);

	public void setLast(String value);

	public void setNumId(String value);

	public void setOnly(Boolean value);

	public void setOnly(String value);

	public void setWeight(Integer value);

	public void setWeight(String value);

}