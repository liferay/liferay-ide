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

package com.liferay.ide.upgrade.tasks.core.code;

/**
 * @author Gregory Amerson
 */
public class SetupLiferayWorkspaceStepKeys {

	public static final String DESCRIPTION =
		"Create a new Liferay Workspace matching the Target Liferay Version you selected in the new " +
		"Liferay Upgrade Plan wizard. Your Liferay Workspace version will be updated to the version that has been " +
		"set in your Workspace's <i>gradle.properties</i> file.";

	public static final String ID = "setup_liferay_workspace";

	public static final String TITLE = "Setup Liferay Workspace";

}