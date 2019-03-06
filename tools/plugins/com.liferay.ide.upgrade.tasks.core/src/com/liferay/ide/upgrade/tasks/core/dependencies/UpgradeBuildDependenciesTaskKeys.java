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

package com.liferay.ide.upgrade.tasks.core.dependencies;

/**
 * @author Terry Jia
 */
public class UpgradeBuildDependenciesTaskKeys {

	public static final String DESCRIPTION =
		"Plugins rely on their dependencies' availability at compile time and run time.\n Some classes are listed as " +
		"undefined classes or unresolved symbols in your legacy projects because they've been moved, renamed, or " +
		"removed. As a part of modularization in Liferay Portal, many of these classes reside in new modules. To run " +
		"your project, the container must be able to find all your plugins.";

	public static final String ID = "upgrade_build_dependencies";

	public static final String TITLE = "Upgrade Build Dependencies";

}