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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.StringUtil;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Greg Amerson
 */
public interface ILiferayTomcatConstants
{
    @SuppressWarnings( "deprecation" )
    IEclipsePreferences _defaultPrefs = new DefaultScope().getNode( LiferayTomcatPlugin.PLUGIN_ID );

    String DEFAULT_AUTO_DEPLOY_INTERVAL = _defaultPrefs.get( "default.auto.deploy.interval", "500" ); //$NON-NLS-1$ //$NON-NLS-2$

    String DEFAULT_AUTO_DEPLOYDIR = "../deploy"; //$NON-NLS-1$

    String DEFAULT_DEPLOYDIR = "webapps"; //$NON-NLS-1$

    String DEFAULT_MEMORY_ARGS = _defaultPrefs.get( "default.memory.args", "-Xmx1024m -XX:MaxPermSize=256m" ); //$NON-NLS-1$ //$NON-NLS-2$

    String DEFAULT_USER_TIMEZONE = _defaultPrefs.get( "default.user.timezone", "GMT" ); //$NON-NLS-1$ //$NON-NLS-2$

    String[] LIB_EXCLUDES = _defaultPrefs.get( "tomcat.lib.excludes", StringUtil.EMPTY ).split( StringUtil.COMMA ); //$NON-NLS-1$

    boolean PREVENT_MULTI_EXT_PLUGINS_DEPLOY = _defaultPrefs.getBoolean( "prevent.multi.ext.plugins.deploy", false ); //$NON-NLS-1$
}
