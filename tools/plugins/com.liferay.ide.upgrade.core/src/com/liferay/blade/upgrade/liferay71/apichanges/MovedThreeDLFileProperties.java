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

package com.liferay.blade.upgrade.liferay71.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Charles Wu
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Moved Three DL File Properties to OSGi Configuration",
	"problem.summary=Two DL File properties have been moved from Server Administration", "problem.tickets=LPS-69208",
	"problem.section=#moved-three-dl-file-properties", "implName=MovedThreeDLFileProperties", "version=7.1"
},
	service = FileMigrator.class)
public class MovedThreeDLFileProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("dl.file.entry.previewable.processor.max.size");
		properties.add("dl.file.extensions");
		properties.add("dl.file.max.size");
	}

}