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
public class CopyPluginsSDKProjectsToWorkspaceStepKeys {

	public static final String DESCRIPTION =
		"<p>During any Liferay upgrade, it is a good time to consider which existing plugins you want to continue " +
		"supporting when building for Liferay 7.x and greater. You may opt to not migrate some plugins to your new " +
		"workspace.  In this step, you will select which existing Plugins SDK 6.x plugins you wish to copy to your " +
		"new workspace to continue with the upgrade process (e.g., converting or adapting them to new APIs).";

	public static final String ID = "move_plugins_sdk_projects_to_workspace";

	public static final String TITLE = "Copy plugins from 6.x Plugins SDK to Workspace";

}