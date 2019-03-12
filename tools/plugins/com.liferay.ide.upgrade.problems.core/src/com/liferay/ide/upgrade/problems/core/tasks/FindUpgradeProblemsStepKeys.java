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
public class FindUpgradeProblemsStepKeys {

	public static final String DESCRIPTION =
		"<p>Peform this action to find upgrade problems in Java, JSP, XML, and properties in your workspace. For " +
		"each problem that is found, a marker will be indicate by location in the source file that requires an " +
		"upgrade. These markers will be displayed in the following locations:<li>Project Explorer > Liferay Upgrade " +
		"Problems node</li><li>In each source file as a marker annotation (sidebar gutter or underlined)</li><li>" +
		"Markers view under the heading <b>Liferay Upgrade Problem</b></li>When you select an upgrade problem, you " +
		"can see relevant documentation on how to fix the problem in the <b>Liferay Upgrade Plan Info View</b></p>";

	public static final String ID = "find_upgrade_problems";

	public static final String TITLE = "Find Upgrade Problems";

}