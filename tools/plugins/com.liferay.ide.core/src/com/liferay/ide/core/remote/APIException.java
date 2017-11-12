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

package com.liferay.ide.core.remote;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class APIException extends Exception {

	public APIException(String api, Exception e) {
		super(e);

		_api = api;
		_msg = e.getMessage();
	}

	public APIException(String api, String msg) {
		_api = api;
		_msg = msg;
	}

	@Override
	public String getMessage() {
		return "API: " + _api + " " + _msg;
	}

	private String _api;
	private String _msg;

}