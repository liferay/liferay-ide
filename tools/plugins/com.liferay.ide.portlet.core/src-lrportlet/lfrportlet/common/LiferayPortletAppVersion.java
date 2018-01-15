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
 * @author Kamesh Sampath
 */
@Label(standard = "Liferay Portlet Application Version")
public enum LiferayPortletAppVersion {

	@Label(standard = "6.1.0")
	@EnumSerialization(primary = "6.1.0")
	v_6_1_0,

	@Label(standard = "6.0.0")
	@EnumSerialization(primary = "6.0.0")
	v_6_0_0
}