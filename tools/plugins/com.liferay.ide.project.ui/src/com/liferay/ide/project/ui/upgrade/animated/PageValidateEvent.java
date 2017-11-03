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

package com.liferay.ide.project.ui.upgrade.animated;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class PageValidateEvent {

	public static String error = "error";
	public static String warning = "warning";

	public String getMessage() {
		return _message;
	}

	public String getPageId() {
		return _pageId;
	}

	public String getType() {
		return _type;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setPageId(String pageId) {
		_pageId = pageId;
	}

	public void setType(String type) {
		_type = type;
	}

	private String _message;
	private String _pageId;
	private String _type = error;

}