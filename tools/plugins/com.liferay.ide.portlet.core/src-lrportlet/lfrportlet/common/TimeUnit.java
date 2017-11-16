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
@Label(standard = "Time Unit")
public enum TimeUnit {

	@Label(standard = "second")
	@EnumSerialization(primary = "second")
	second,

	@Label(standard = "minute")
	@EnumSerialization(primary = "minute")
	minute,

	@Label(standard = "hour")
	@EnumSerialization(primary = "hour")
	hour,

	@Label(standard = "day")
	@EnumSerialization(primary = "day")
	day,

	@Label(standard = "week")
	@EnumSerialization(primary = "week")
	week

}