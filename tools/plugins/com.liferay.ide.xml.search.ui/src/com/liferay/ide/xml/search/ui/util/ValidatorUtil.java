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

package com.liferay.ide.xml.search.ui.util;

/**
 * @author Kuo Zhang
 */
public class ValidatorUtil {

	public static boolean isChar(char c) {
		int x = c;

		if (((x >= 97) && (x <= 122)) || ((x >= 65) && (x <= 90))) {
			return true;
		}

		return false;
	}

	public static boolean isValidNamespace(String namespace) {
		for (char c : namespace.toCharArray()) {
			if ((c != '_') && !isChar(c)) {
				return false;
			}
		}

		return true;
	}

}