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

package com.liferay.ide.upgrade.problems.core.internal.liferay72;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=properties", "problem.title=Moved Two Staging Properties to OSGi Configuration",
		"problem.summary=Two Staging properties have been moved from portal.properties to an OSGi configuration named ExportImportServiceConfiguration.java in the export-import-service module.",
		"problem.tickets=LPS-88018", "problem.section=#moved-staging-properties", "problem.version=7.2", "version=7.2"
	},
	service = FileMigrator.class
)
public class MovedStagingProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("staging.delete.temp.lar.on.failure");
		properties.add("staging.delete.temp.lar.on.success");
	}

}