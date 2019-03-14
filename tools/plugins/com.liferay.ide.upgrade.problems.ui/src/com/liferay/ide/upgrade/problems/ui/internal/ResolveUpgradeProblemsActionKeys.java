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

package com.liferay.ide.upgrade.problems.ui.internal;

/**
 * @author Gregory Amerson
 */
public class ResolveUpgradeProblemsActionKeys {

	public static final String DESCRIPTION =
		"<p>Now that the upgrade problems have been located, perform this to display results in the <b>Project " +
		"Explorer > Liferay Upgrade Problems</b> node. Then as you select each upgrade problem, the documentation " +
		"for how to adapt your code will be displayed in the <b>Liferay Upgrade Plan Info View</b>.<br/><br/>For each " +
		"upgrade problem node, you can mark them as resolved/skipped using the context menu. Leave this step marked " +
		"as INCOMPLETE until after you have resolved all upgrade problems accordingly.</p>";

	public static final String ID = "resole_upgrade_problems";

	public static final String TITLE = "Resolve Upgrade Problems";

}