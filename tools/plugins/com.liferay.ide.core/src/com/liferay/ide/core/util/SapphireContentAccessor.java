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

package com.liferay.ide.core.util;

import org.eclipse.sapphire.Value;

/**
 * @author Gregory Amerson
 */
public interface SapphireContentAccessor {

	public default <T> T get(Value<T> value) {
		if (value != null) {
			return value.content();
		}

		return null;
	}

	public default <T> T get(Value<T> value, boolean useDefaultValue) {
		if (value != null) {
			return value.content(useDefaultValue);
		}

		return null;
	}

}