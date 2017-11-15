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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Kuo Zhang
 */
public class ReflectionUtil {

	public static Field getDeclaredField(Class<?> clazz, String fieldName, boolean includeSuper) {
		try {
			return clazz.getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException nsfe) {
			if (includeSuper && (clazz.getSuperclass() != null)) {
				return getDeclaredField(clazz.getSuperclass(), fieldName, true);
			}
		}
		catch (Exception e) {
		}

		return null;
	}

	public static Method getDeclaredMethod(
		Class<?> clazz, String methodName, boolean includeSuper, Class<?>... parameterTypes) {

		try {
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException nsme) {
			if (includeSuper && (clazz.getSuperclass() != null)) {
				return getDeclaredMethod(clazz.getSuperclass(), methodName, true, parameterTypes);
			}
		}
		catch (Exception e) {
		}

		return null;
	}

}