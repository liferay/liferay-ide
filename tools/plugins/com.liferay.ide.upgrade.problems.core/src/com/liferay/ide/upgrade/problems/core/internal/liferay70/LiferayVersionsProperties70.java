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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=liferay-versions key in Liferay Plugin Packages Properties",
	"problem.summary=In order to deploy this project to 7.0 the liferay-versions property must be set to 7.0.0+",
	"problem.tickets=", "problem.section=", "auto.correct=property", "implName=LiferayVersionsProperties", "version=7.0"
},
	service = {AutoFileMigrator.class, FileMigrator.class})
public class LiferayVersionsProperties70 extends BaseLiferayVersionsProperties {

	public LiferayVersionsProperties70() {
		super(".*7\\.[0-9]\\.[0-9].*", "7.0.0+");
	}

}