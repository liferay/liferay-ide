/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;



/**
 * @author kamesh
 */
public interface ILiferayServerConstants
{

	IEclipsePreferences _defaultPrefs = new DefaultScope().getNode( LiferayServerCorePlugin.PLUGIN_ID );

	String DEFAULT_AUTO_DEPLOY_INTERVAL = _defaultPrefs.get( "default.auto.deploy.interval", "500" );

	String DEFAULT_AUTO_DEPLOYDIR = "../deploy";

	String DEFAULT_DEPLOYDIR = "webapps";

	String DEFAULT_MEMORY_ARGS = _defaultPrefs.get( "default.memory.args", "-Xmx1024m -XX:MaxPermSize=256m -Dfile.encoding=UTF-8 -Duser.timezone=GMT" );

	String DEFAULT_USER_TIMEZONE = _defaultPrefs.get( "default.user.timezone", "GMT" );

	String[] LIB_EXCLUDES = _defaultPrefs.get( "tomcat.lib.excludes", "" ).split( "," );

	boolean PREVENT_MULTI_EXT_PLUGINS_DEPLOY = _defaultPrefs.getBoolean( "prevent.multi.ext.plugins.deploy", false );
}
