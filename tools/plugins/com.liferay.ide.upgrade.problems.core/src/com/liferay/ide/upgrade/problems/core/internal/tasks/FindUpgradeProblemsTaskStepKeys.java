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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

/**
 * @author Gregory Amerson
 */
public class FindUpgradeProblemsTaskStepKeys {

	public static final String DESCRIPTION =
		"This step will help find breaking changes in Java, JSP, XML and properties files by adding problem markers " +
		"to the problematic resources. Once upgrade problems are found, you can go to the Project Explorer to browse " +
		"the upgrade problems and see associated breaking change documentation.";

	public static final String TITLE = "Find Upgrade Problems";

}