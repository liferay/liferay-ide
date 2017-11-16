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

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author kamesh
 */
@Label(standard = "Control Panel Entry Category")
public enum ControlPanelEntryCategory {

	@Label(standard = "my")
	@EnumSerialization(primary = "my")
	my,

	@Label(standard = "content")
	@EnumSerialization(primary = "content")
	content,

	@Label(standard = "portal")
	@EnumSerialization(primary = "portal")
	portal,

	@Label(standard = "server")
	@EnumSerialization(primary = "server")
	server

}