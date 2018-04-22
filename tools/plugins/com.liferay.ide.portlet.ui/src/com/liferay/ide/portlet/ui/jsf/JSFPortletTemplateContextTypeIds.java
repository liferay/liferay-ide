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

package com.liferay.ide.portlet.ui.jsf;

/**
 * @author Gregory Amerson
 */
public class JSFPortletTemplateContextTypeIds {

	public static final String ALL = _getAll();

	public static final String ATTRIBUTE = _getAttribute();

	public static final String ATTRIBUTE_VALUE = _getAttributeValue();

	public static final String NEW = _getNew();

	public static final String NEW_TAG = "tag_new";

	public static final String TAG = _getTag();

	private static String _getAll() {
		return _getPrefix() + "_all";
	}

	private static String _getAttribute() {
		return _getPrefix() + "_attribute";
	}

	private static String _getAttributeValue() {
		return _getPrefix() + "_attribute_value";
	}

	private static String _getNew() {
		return _getPrefix() + "_new";
	}

	private static String _getPrefix() {
		return "jsf_portlet";
	}

	private static String _getTag() {
		return _getPrefix() + "_tag";
	}

}