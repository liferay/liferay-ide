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

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
@Label(standard = "scale type")
public enum TimeScaleType {

	@Label(standard = "second")
		@EnumSerialization(primary = "second")
	SECOND,

	@Label(standard = "minute")
		@EnumSerialization(primary = "minute")
	MINUTE,

	@Label(standard = "hour")
		@EnumSerialization(primary = "hour")
	HOUR,

	@Label(standard = "day")
		@EnumSerialization(primary = "day")
	DAY,

	@Label(standard = "week")
		@EnumSerialization(primary = "week")
	WEEK,

	@Label(standard = "year")
		@EnumSerialization(primary = "year")
	YEAR,

}