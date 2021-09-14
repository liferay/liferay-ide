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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ethan Sun
 */
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Remove some static methods in com.liferay.portal.kernel.servlet.SanitizedServletResponse and Portal Property http.header.secure.x.xss.protection",
		"problem.summary=Some static methods in `com.liferay.portal.kernel.servlet.SanitizedServletResponse` have been removed because these relate to the X-Xss-Protection header which is not supported by modern browsers",
		"problem.tickets=LPS-134188", "problem.section=#removed-sanitized-servlet-response-some-static-method",
		"version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveXXssProtectionProperty extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("http.header.secure.x.xss.protection");
	}

}