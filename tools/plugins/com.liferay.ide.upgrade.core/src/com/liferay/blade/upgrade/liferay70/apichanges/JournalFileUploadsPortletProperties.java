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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Moved Journal File Uploads Portlet Properties to OSGi Configuration",
	"problem.summary=Moved Journal File Uploads Portlet Properties to OSGi Configuration", "problem.tickets=LPS-69209",
	"problem.section=#moved-journal-file-uploads-portlet-properties-to-osgi-configuration",
	"implName=JournalFileUploadsPortletProperties", "version=7.0"
},
	service = FileMigrator.class)
public class JournalFileUploadsPortletProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("journal.image.extensions");
		properties.add("journal.image.small.max.size");
	}

}