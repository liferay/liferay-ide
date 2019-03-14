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

package com.liferay.ide.upgrade.tasks.core.buildservice;

/**
 * @author Gregory Amerson
 */
public class RebuildServicesStepKeys {

	public static final String DESCRIPTION =
		"In this step, we will delete some legacy service builder related files and then re-run the <i>buildService</i> task " +
		"on service builder projects. Note: Please make sure the default installed JRE is JDK 8 (Preferences -> Java " +
		"-> Installed JREs).";

	public static final String ID = "rebuild_services";

	public static final String TITLE = "Rebuild Liferay Services";

}