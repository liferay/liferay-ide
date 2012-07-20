/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.server.remote;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Greg Amerson
 */
public interface IRemoteConnection
{

    int getDebugPort();

    List<String> getLiferayPlugins();

    String getManagerURI();

    String getPassword();

    String getServerState();

    String getUsername();

    Object installApplication( String absolutePath, String appName, IProgressMonitor monitor );

    boolean isAlive();

    boolean isAppInstalled( String appName );

    boolean isLiferayPluginStarted( String name );

    void setHost( String host );

    void setHttpPort( String port );

    void setManagerContextPath( String path );

    void setPassword( String password );

    void setUsername( String username );

    Object uninstallApplication( String appName, IProgressMonitor monitor );

    Object updateApplication( String appName, String absolutePath, IProgressMonitor monitor );

}
