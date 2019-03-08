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

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Charles Wu
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Moved Organization Type Properties to OSGi Configuration",
	"problem.summary=The organization type properties have been moved from portal.properties",
	"problem.tickets=LPS-77183", "problem.section=#moved-organization-type-properties", "version=7.1"
},
	service = FileMigrator.class)
public class MovedOrganizationTypeProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("organizations.types");
		properties.add("organizations.rootable");
		properties.add("organizations.children.types");
		properties.add("organizations.country.enabled");
		properties.add("organizations.country.required");
	}

}