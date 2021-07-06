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

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * @author Terry Jia
 */
public class ListUtil {

	public static boolean contains(List<?> list, Object o) {
		if ((list == null) || (o == null)) {
			return false;
		}

		return list.contains(o);
	}

	public static boolean contains(Set<?> set, Object o) {
		if ((set == null) || (o == null)) {
			return false;
		}

		return set.contains(o);
	}

	public static boolean contains(Set<?> set, Set<?> objSet) {
		if ((set == null) || (objSet == null)) {
			return false;
		}

		return set.containsAll(objSet);
	}

	public static boolean isEmpty(Collection<?> collection) {
		if ((collection == null) || collection.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(Enumeration<?> enumeration) {
		if ((enumeration == null) || !enumeration.hasMoreElements()) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(Set<?> set) {
		if ((set == null) || set.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isMultiple(Collection<?> collection) {
		if (isNotEmpty(collection) && (collection.size() > 1)) {
			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static boolean isNotEmpty(Enumeration<?> enumeration) {
		return !isEmpty(enumeration);
	}

	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	public static boolean isNotEmpty(Object[] array) {
		return !isEmpty(array);
	}

	public static boolean isNotEmpty(Set<?> set) {
		return !isEmpty(set);
	}

	public static boolean notContains(Set<?> set, Object o) {
		return !contains(set, o);
	}

	public static boolean sizeEquals(Collection<?> collection1, Collection<?> collection2) {
		if (isEmpty(collection1) && isEmpty(collection2)) {
			return true;
		}

		if (collection1.size() == collection2.size()) {
			return true;
		}

		return false;
	}

}
