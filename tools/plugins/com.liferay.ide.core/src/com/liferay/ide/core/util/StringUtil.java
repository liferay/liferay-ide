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

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class StringUtil {

	public static boolean contains(String s1, String s2) {
		if ((s1 == null) || (s2 == null)) {
			return false;
		}

		return s1.contains(s2);
	}

	public static boolean endsWith(Object o, String s2) {
		if ((o == null) || (s2 == null)) {
			return false;
		}

		String s1 = o.toString();

		return s1.endsWith(s2);
	}

	public static boolean endsWith(String s1, String s2) {
		if ((s1 == null) || (s2 == null)) {
			return false;
		}

		return s1.endsWith(s2);
	}

	public static boolean equals(String s1, Object o) {
		if ((s1 == null) || (o == null)) {
			return false;
		}

		return s1.equals(o.toString());
	}

	public static boolean equals(String s1, String s2) {
		if ((s1 == null) || (s2 == null)) {
			return false;
		}

		return s1.equals(s2);
	}

	public static boolean isQuoted(String string) {
		if ((string == null) || (string.length() < 2)) {
			return false;
		}

		int lastIndex = string.length() - 1;
		char firstChar = string.charAt(0);
		char lastChar = string.charAt(lastIndex);

		if (((firstChar == StringPool.SINGLE_QUOTE_CHAR) && (lastChar == StringPool.SINGLE_QUOTE_CHAR)) ||
			((firstChar == StringPool.DOUBLE_QUOTE_CHAR) && (lastChar == StringPool.DOUBLE_QUOTE_CHAR))) {

			return true;
		}

		return false;
	}

	public static String merge(String[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder(2 * array.length - 1);

		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(delimiter);
			}

			sb.append(array[i]);
		}

		return sb.toString();
	}

	public static String replace(String content, String source, String target) {
		if (content == null) {
			return null;
		}

		int length = content.length();
		int position = 0;
		int previous = 0;
		int spacer = source.length();

		StringBuffer sb = new StringBuffer();

		while (((position + spacer - 1) < length) && (content.indexOf(source, position) > -1)) {
			position = content.indexOf(source, previous);

			sb.append(content.substring(previous, position));

			sb.append(target);

			position += spacer;
			previous = position;
		}

		sb.append(content.substring(position, content.length()));

		return sb.toString();
	}

	public static boolean startsWith(String s1, String s2) {
		if ((s1 == null) || (s2 == null)) {
			return false;
		}

		return s1.startsWith(s2);
	}

	public static String trim(String string) {
		if (string == null) {
			return null;
		}

		return string.trim();
	}

	public static String trim(StringBuffer sb) {
		if (sb == null) {
			return null;
		}

		String string = sb.toString();

		return string.trim();
	}

}