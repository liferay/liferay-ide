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

package com.liferay.ide.portal.core.debug.util;

import com.liferay.ide.core.util.StringPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dante Wang
 */
public class StringUtil {

	public static String extract(
		String string, String beginDelimiter, String endDelimiter) {

		return extract(string, beginDelimiter, endDelimiter, 0);
	}

	public static String extract(
		String string, String beginDelimiter, String endDelimiter,
		int fromIndex) {

		if (isNull(string) || beginDelimiter == null ||
				beginDelimiter.isEmpty()) {

			return StringPool.BLANK;
		}

		int beginIndex =
			string.indexOf(beginDelimiter) + beginDelimiter.length();
		int endIndex = string.length();

		if (endDelimiter != null || !endDelimiter.isEmpty()) {
			endIndex = string.indexOf(endDelimiter);
		}

		if (beginIndex >= endIndex) {
			return StringPool.BLANK;
		}

		return string.substring(beginIndex, endIndex);
	}

	public static boolean isNotNull(String string) {
		return !isNull(string);
	}

	public static boolean isNull(String string) {
		if((string == null) || string.isEmpty()) {
			return true;
		}

		string = string.trim();

		if (string.isEmpty()) {
			return true;
		}

		return false;
	}

	public static List<String> split(String string) {
		return split(string, StringPool.COMMA);
	}

	public static List<String> split(String string, String delimiter) {
		if(isNull(string) || delimiter == null || delimiter.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		List<String> nodeValues = new ArrayList<String>();

		int offset = 0;
		int pos = string.indexOf(delimiter, offset);

		while (pos != -1) {
			String nodeValue = string.substring(offset, pos);

			nodeValue = nodeValue.trim();

			if (!nodeValue.isEmpty()) {
				nodeValues.add(nodeValue);
			}

			offset = pos + 1;
			pos = string.indexOf(delimiter, offset);
		}

		if (offset < string.length()) {
			String nodeValue = string.substring(offset);

			nodeValue = nodeValue.trim();

			if (!nodeValue.isEmpty()) {
				nodeValues.add(nodeValue);
			}
		}

		return nodeValues;
	}

	public static String replace(String string, String oldSub, String newSub) {
		return replace(string, oldSub, newSub, 0);
	}

	public static String replace(
		String string, String oldSub, String newSub, int fromIndex) {

		if (string == null) {
			return null;
		}

		if ((oldSub == null) || oldSub.equals(StringPool.BLANK)) {
			return string;
		}

		if (newSub == null) {
			newSub = StringPool.BLANK;
		}

		int y = string.indexOf(oldSub, fromIndex);

		if (y < 0) {
			return string;
		}

		StringBuilder sb = new StringBuilder();

		int length = oldSub.length();
		int x = 0;

		while (x <= y) {
			sb.append(string.substring(x, y));
			sb.append(newSub);

			x = y + length;
			y = string.indexOf(oldSub, x);
		}

		sb.append(string.substring(x));

		return sb.toString();
	}

}
