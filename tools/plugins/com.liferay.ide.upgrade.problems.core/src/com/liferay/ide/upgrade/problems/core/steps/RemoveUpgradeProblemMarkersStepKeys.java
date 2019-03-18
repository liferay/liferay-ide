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

package com.liferay.ide.upgrade.problems.core.steps;

/**
 * @author Gregory Amerson
 */
public class RemoveUpgradeProblemMarkersStepKeys {

	public static final String DESCRIPTION =
		"<p>After auto correcting upgrade problems, all of the previously found markers must be removed because in " +
		"most cases, the line number or other marker information is out of date and must be removed before " +
		"continuing.</p>";

	public static final String ID = "remove_upgrade_problems_markers";

	public static final String TITLE = "Remove Upgrade Problems Markers";

}