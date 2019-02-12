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

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Removed USERS_LAST_NAME_REQUIRED from portal.properties",
	"problem.summary=The USERS_LAST_NAME_REQUIRED property has been removed from portal.properties and the " +
		"corresponding UI. Required names are now handled on a per-language basis via the language.properties files. " +
			"It has also been removed as an option from the Portal Settings section of the Control Panel.",
	"problem.tickets=LPS-54956",
	"problem.section=#removed-userslastnamerequired-from-portal-properties-in-favor-of-language-p",
	"implName=UsersLastNameRequiredProperties", "version=7.0"
},
	service = FileMigrator.class)
public class UsersLastNameRequiredProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("users.last.name.required");
	}

}