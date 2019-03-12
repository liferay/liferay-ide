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

package com.liferay.ide.upgrade.tasks.ui.internal.sdk;

/**
 * @author Gregory Amerson
 */
public class ConvertPluginsSDKProjectsToModulesStepKeys {

	public static final String DESCRIPTION =
		"<p>Now that you have selected which plugins you need to migrate, you may now choose which projects you plan " +
		"on converting. There are many things to consider when planning this step. Some plugins you are required to " +
		"convert to modules such as custom JSPs or EXT plugins. Also, some plugins it is highly recommended that you " +
		"convert to modules to best take advantage of the new modular framework in Liferay, e.g. Service Builder " +
		"portlets.<br/><br/>Click <b>Perform</b> to select hich projects you wish to convert. The Upgrade Planner " +
		"will convert each of them to a gradle module that lives inside either the <b>modules</b> or <b>wars</b> " +
		"directory of your workspace depending on the type of plugin you are converting.<br/<br/></p>";

	public static final String ID = "convert_plugins_sdk_projects_to_modules";

	public static final String TITLE = "Convert 6.x Plugins SDK Projects to Modules";

}