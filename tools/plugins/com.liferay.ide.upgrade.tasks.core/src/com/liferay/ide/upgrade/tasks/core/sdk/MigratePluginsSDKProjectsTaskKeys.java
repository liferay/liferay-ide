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

package com.liferay.ide.upgrade.tasks.core.sdk;

/**
 * @author Gregory Amerson
 */
public class MigratePluginsSDKProjectsTaskKeys {

	public static final String DESCRIPTION =
		"<p>The Plugins SDK is deprecated as of Liferay Portal CE 7.0. So, during this upgrade process, you have the " +
		"following options.<li>Convert your plugins to workspace modules</li><li>Adapt your Plugins SDK style plugin " +
		"to use a new legacy Plugins SDK for 7.x</li>This allows you to choose how to best upgrade your plugins in" +
		"way that works best, either converting to modules to leaveing as traditional plugins. In some cases " +
		"converting to modules will be recommended (i.e. service-builder portlets) and sometimes it will be required " +
		"(i.e. custom jsps or ext).</p>";

	public static final String ID = "migrate_plugins_sdk_projects";

	public static final String TITLE = "Migrate Plugins SDK Projects";

}