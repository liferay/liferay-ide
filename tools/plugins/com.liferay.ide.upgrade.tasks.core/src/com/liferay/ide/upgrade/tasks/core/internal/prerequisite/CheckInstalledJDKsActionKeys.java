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
		"To make sure you have a compatible JDK installed and configured, we need to perform a check " +
		"on this Eclipse installation to make sure that <ol><li>There is a JDK8 VM available</li><li>That JDK is " +
		"set as the default.</li></ol> Select <b>Click to perform</b> to check for these requirements. If there is an error " +
		"message, you must go to the Preferences > Java > Installed JREs menu and add a JRE entry that points to a JDK8 " +
		"installation and set it as the default.";

	public static final String ID = "check_installed_jdks";

	public static final String TITLE = "Check Installed JDKs";

}