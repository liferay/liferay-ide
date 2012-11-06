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

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCorePlugin;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.server.core.model.IURLProvider;

/**
 * @author Greg Amerson
 */
public interface IRemoteServer extends ILiferayServer, IURLProvider
{

    @SuppressWarnings( "deprecation" )
    public static final IEclipsePreferences defaultPrefs =
        new DefaultScope().getNode( LiferayServerCorePlugin.PLUGIN_ID );

    String ATTR_ADJUST_DEPLOYMENT_TIMESTAMP = "adjust-deployment-timestamp";

    String ATTR_HOSTNAME = "hostname";

    String ATTR_HTTP_PORT = "http-port";

    String ATTR_LIFERAY_PORTAL_CONTEXT_PATH = "liferay-portal-context-path";

    String ATTR_PASSWORD = "password";

    String ATTR_SERVER_MANAGER_CONTEXT_PATH = "server-manager-context-path";

    String ATTR_USERNAME = "username";

    boolean DEFAULT_ADJUST_DEPLOYMENT_TIMESTAMP = defaultPrefs.getBoolean( "adjust.deployment.timestamp", true );

    String DEFAULT_HTTP_PORT = defaultPrefs.get( "default.http.port", "" );

    String DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH = defaultPrefs.get( "default.liferay.portal.context.path", "" );

    String DEFAULT_PASSWORD = defaultPrefs.get( "default.password", "" );

    String DEFAULT_SERVER_MANAGER_CONTEXT_PATH = defaultPrefs.get( "default.server.manager.context.path", "" );

    String DEFAULT_USERNAME = defaultPrefs.get( "default.username", "" );

    boolean getAdjustDeploymentTimestamp();

    String getHost();

    String getHTTPPort();

    String getId();

    String getLiferayPortalContextPath();

    String getPassword();

    String getServerManagerContextPath();

    String getUsername();

    void setAdjustDeploymentTimestamp( boolean adjustDemploymentTimestamp );

}
