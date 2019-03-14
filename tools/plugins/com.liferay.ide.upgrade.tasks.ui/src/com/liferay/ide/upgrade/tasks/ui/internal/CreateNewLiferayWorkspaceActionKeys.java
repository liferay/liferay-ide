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

package com.liferay.ide.upgrade.tasks.ui.internal;

/**
 * @author Gregory Amerson
 */
public class CreateNewLiferayWorkspaceActionKeys {

	public static final String DESCRIPTION =
		"<p>Starting in Liferay 7.0 and greater, the recommended way to build Liferay projects is to use a Liferay " +
		"Workspace, which is a generated project scaffolding with associated Gradle or Maven plugins. To see more " +
		"information, view the associated documentation in the <b>Liferay Upgrade Plan Info</b> view.<br/><br/>To " +
		"begin the code upgrade process, first create a new Liferay Workspace by selecting <b>Click to perform</b> below.<br/>" +
		"<br/><b>Note: this tool only supports creating Gradle workspaces at this time.</b></p>";

	public static final String ID = "create_new_liferay_workspace";

	public static final String TITLE = "Create New Liferay Workspace";

	public static final String URL =
		"https://raw.githubusercontent.com/liferay/liferay-docs/7.1.x/develop/tutorials/articles/100-tooling/02-liferay-ide/02-creating-a-liferay-workspace-with-liferay-ide.markdown";

}