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

package com.liferay.ide.upgrade.tasks.core.internal.prerequisite;

/**
 * @author Gregory Amerson
 */
public class CheckInstalledJDKsActionKeys {

	public static final String DESCRIPTION =
		"In order to make sure that you have a complatible JDK installed and configured, we need to perform a check " +
		"on this Eclipse installation to make sure that 1) There is a JDK8 VM available, 2) that it is set as the " +
		"default.  \"Click perform\" to check.  IF there is an error message, then you must go to the " +
		"Prefernces > Java > Installed JREs and add a JRE entry that points to a JDK8 installation and set it as the " +
		"default.  Then restart this Before contining an update plan, it is recommended to update to the latest version of Liferay Developer " +
		"Studio or Liferay IDE (whichever version you are using).  Click \"Perform\" to check for updates.  If there " +
		"is an update available, installed it, and afterwards you can return ot this step in upgrade plan and " +
		"continue the upgrade plan.<br/><br/>This Action has not been implemented, select \"Click to Complete\" to " +
		"continue upgrade plan.";

	public static final String ID = "check_installed_jdks";

	public static final String TITLE = "Check Installed JDKs";

}