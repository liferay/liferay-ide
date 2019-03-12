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

package com.liferay.ide.upgrade.problems.core.tasks;

/**
 * @author Gregory Amerson
 */
public class FixUpgradeProblemsTaskKeys {

	public static final String DESCRIPTION =
		"<p>Now that you have your projects in your workspace, it is time to find and fix all of the known upgrade " +
		"problems that you will encounter. The steps below will help you address each problem either automatically " +
		"or provide the associated breaking change documentation with instructions on how to manually adapt your " +
		"code.<br/><br/>This task will not find 100% of all upgrade problems, but we plan to continue improving the " +
		"detection and provide additional steps for new required upgrades. Please contact Liferay Devtools team if " +
		"your upgrade process identified additional problems that needed to be fixed and we can try to incorporate " +
		"those steps in a future version of this planner.</p>";

	public static final String ID = "fix_upgrade_problems";

	public static final String TITLE = "Fix Upgrade Problems";

}