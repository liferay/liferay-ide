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

package com.liferay.ide.idea.util;

/**
 * @author Terry Jia
 */
public class CoreUtil {

	public static boolean isNullOrEmpty(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		return false;
	}

	public static boolean isNullOrEmpty(String val) {
		String trimmedVal = val.trim();

		if ((val == null) || val.equals(StringPool.EMPTY) || trimmedVal.equals(StringPool.EMPTY)) {
			return true;
		}

		return false;
	}

}