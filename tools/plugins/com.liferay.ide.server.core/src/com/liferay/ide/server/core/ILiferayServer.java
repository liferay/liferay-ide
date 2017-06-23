/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.server.core;

import com.liferay.ide.core.util.StringPool;

import java.net.URL;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Greg Amerson
 * @author Terry Jia
 */
public interface ILiferayServer
{

    IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode( LiferayServerCore.PLUGIN_ID );

    String ATTR_PASSWORD = "password";

    String ATTR_USERNAME = "username";

    String ATTR_HTTP_PORT = "http-port";

    String DEFAULT_PASSWORD = defaultPrefs.get( "default.password", StringPool.EMPTY );

    String DEFAULT_USERNAME = defaultPrefs.get( "default.username", StringPool.EMPTY );

    URL getWebServicesListURL();

    String getPassword();

    URL getPluginContextURL( String context );

    URL getPortalHomeUrl();

    String getUsername();

    String getHttpPort();

    String getId();

    String getHost();
}
