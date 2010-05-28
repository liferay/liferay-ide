/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.tomcat.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Greg Amerson
 */
public interface IPortalTomcatConstants {

	public static final IEclipsePreferences defaultPrefs = new DefaultScope().getNode(PortalTomcatPlugin.PLUGIN_ID);

	final String DEFAULT_AUTO_DEPLOY_INTERVAL = "500";

	final String DEFAULT_AUTO_DEPLOYDIR = "../deploy";

	final String DEFAULT_DEPLOYDIR = "webapps";

	final String[] LIB_EXCLUDES = defaultPrefs.get("tomcat.lib.excludes", "").split(",");

	/**
	 * Property which specifies the directory where liferay scans for
	 * autodeployment
	 */
	final String PROPERTY_AUTO_DEPLOY_DIR = "autoDeployDir";

	final String PROPERTY_AUTO_DEPLOY_INTERVAL = "autoDeployInterval";
}
