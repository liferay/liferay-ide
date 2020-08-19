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

package com.liferay.ide.upgrade.problems.core.internal.liferay71;

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.BaseLiferayVersionsProperties;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=properties", "problem.title=liferay-versions key in Liferay Plugin Packages Properties 7.1",
		"problem.summary=In order to deploy this project to 7.1 the liferay-versions property must be set to 7.1.0+",
		"problem.tickets=", "problem.section=", "auto.correct=property", "version=7.1"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class Liferay71VersionsProperties extends BaseLiferayVersionsProperties {

	public Liferay71VersionsProperties() {
		super(".*7\\.1\\.[0-9].*", "7.1.0+");
	}

}