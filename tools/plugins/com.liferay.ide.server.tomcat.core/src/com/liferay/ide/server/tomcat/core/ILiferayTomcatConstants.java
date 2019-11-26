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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.StringPool;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public interface ILiferayTomcatConstants {

	public static final IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode(LiferayTomcatPlugin.PLUGIN_ID);

	public static final String DEFAULT_AUTO_DEPLOY_INTERVAL = defaultPrefs.get("default.auto.deploy.interval", "500");

	public static final String DEFAULT_AUTO_DEPLOYDIR = "../deploy";

	public static final String DEFAULT_DEPLOYDIR = "webapps";

	public static final String DEFAULT_MEMORY_ARGS = defaultPrefs.get("default.memory.args", "-Xmx2560m");

	public static final boolean DEFAULT_USE_DEFAULT_PORTAL_SERVER_SETTING = false;

	public static final String DEFAULT_USER_TIMEZONE = defaultPrefs.get("default.user.timezone", "GMT");

	public static final int DEVELOPMENT_SERVER_MODE = 2;

	public static final String[] LIB_EXCLUDES = defaultPrefs.get(
		"tomcat.lib.excludes", StringPool.EMPTY
	).split(
		StringPool.COMMA
	);

	public static final boolean PREVENT_MULTI_EXT_PLUGINS_DEPLOY = defaultPrefs.getBoolean(
		"prevent.multi.ext.plugins.deploy", false);

	public static final int STANDARD_SERVER_MODE = 1;

}