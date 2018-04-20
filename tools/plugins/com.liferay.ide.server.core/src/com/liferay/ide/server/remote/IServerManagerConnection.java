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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.remote.IRemoteConnection;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public interface IServerManagerConnection extends IRemoteConnection {

	public int getDebugPort() throws APIException;

	public String getFMDebugPassword() throws APIException;

	public int getFMDebugPort() throws APIException;

	public List<String> getLiferayPlugins();

	public String getManagerURI();

	public String getRemoteServerConfig(String configAPI) throws APIException;

	public String getServerState() throws APIException;

	public Object installApplication(String absolutePath, String appName, IProgressMonitor monitor) throws APIException;

	public boolean isAlive() throws APIException;

	public boolean isAppInstalled(String appName) throws APIException;

	public boolean isLiferayPluginStarted(String name) throws APIException;

	public void setManagerContextPath(String path);

	public Object uninstallApplication(String appName, IProgressMonitor monitor) throws APIException;

	public Object updateApplication(String appName, String absolutePath, IProgressMonitor monitor) throws APIException;

}