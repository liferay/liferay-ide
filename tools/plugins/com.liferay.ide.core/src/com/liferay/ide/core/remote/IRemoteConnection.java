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
public interface IRemoteConnection {

	public String getHost();

	public int getHttpPort();

	public String getPassword();

	public String getUsername();

	public void setHost(String host);

	public void setHttpPort(String httpPort);

	public void setPassword(String password);

	public void setUsername(String username);

	public String API = "/api/jsonws";

}