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
	"file.extensions=properties", "problem.title=Moved OpenOffice Properties to OSGi Configuration",
	"problem.summary=The OpenOffice properties have been moved from Server Administration", "problem.tickets=LPS-71382",
	"problem.section=#moved-openOffice-properties", "implName=MovedOpenOfficeProperties", "version=7.1"
},
	service = FileMigrator.class)
public class MovedOpenOfficeProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("openoffice.cache.enabled");
		properties.add("openoffice.server.enabled");
		properties.add("openoffice.server.host");
		properties.add("openoffice.server.port");
	}

}