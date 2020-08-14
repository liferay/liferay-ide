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

package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.PluginsSDKNameDefaultValueService;
import com.liferay.ide.project.core.model.internal.PluginsSDKNamePossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;

/**
 * @author Simon Jiang
 */
public interface LiferayPluginSDKOp extends Element {

	public ElementType TYPE = new ElementType(LiferayPluginSDKOp.class);

	public Value<String> getPluginsSDKName();

	public void setPluginsSDKName(String value);

	@Label(standard = "Plugins SDK")
	@Services(
		{
			@Service(impl = PluginsSDKNamePossibleValuesService.class),
			@Service(impl = PluginsSDKNameDefaultValueService.class)
		}
	)
	public ValueProperty PROP_PLUGINS_SDK_NAME = new ValueProperty(TYPE, "PluginsSDKName");

}