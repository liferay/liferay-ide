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
		"In this task, you will find source code that has been affected by breaking changes in the new Liferay " +
		"version. For each problem that is found, the associated breaking change documentation will be shown to " +
		"help you fix the problems.  In many cases the problems can be fixed automatically.  See more in Auto " +
		"Correct Upgrade Problems step.\nFor all breaking changes that can not be fixed automatically use the " +
		"provided breaking changes documentation to manually fix the code to complete the upgrade.";

	public static final String ID = "fix_upgrade_problems";

	public static final String TITLE = "Fix Upgrade Problems";

}