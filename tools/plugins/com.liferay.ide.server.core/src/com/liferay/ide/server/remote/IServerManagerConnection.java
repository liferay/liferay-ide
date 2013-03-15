/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/


package com.liferay.ide.server.remote;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.remote.IRemoteConnection;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Gregory Amerson
 */
public interface IServerManagerConnection extends IRemoteConnection
{

    int getDebugPort() throws APIException;
    
    String getManagerURI();

    List<String> getLiferayPlugins();

    String getServerState() throws APIException;

    Object installApplication( String absolutePath, String appName, IProgressMonitor monitor ) throws APIException;

    boolean isAlive() throws APIException;

    boolean isAppInstalled( String appName ) throws APIException;

    boolean isLiferayPluginStarted( String name ) throws APIException;

    void setManagerContextPath( String path );

    Object uninstallApplication( String appName, IProgressMonitor monitor ) throws APIException;

    Object updateApplication( String appName, String absolutePath, IProgressMonitor monitor ) throws APIException;

}
