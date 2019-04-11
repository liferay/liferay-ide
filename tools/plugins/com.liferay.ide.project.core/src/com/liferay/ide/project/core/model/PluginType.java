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

import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public enum PluginType {

	@Image(path = "images/ext.png")
	@Label(standard = "Ext")
	ext,
	@Image(path = "images/hook.png")
	@Label(standard = "Hook")
	hook,
	@Image(path = "images/layout.png")
	@Label(standard = "Layout Template")
	layouttpl,
	@Image(path = "images/portlet.png")
	@Label(standard = "Portlet")
	portlet,
	@Image(path = "images/service.png")
	@Label(standard = "Service Builder Portlet")
	servicebuilder,
	@Image(path = "images/theme.png")
	@Label(standard = "Theme")
	theme,
	@Image(path = "images/web.png")
	@Label(standard = "Web")
	web

}