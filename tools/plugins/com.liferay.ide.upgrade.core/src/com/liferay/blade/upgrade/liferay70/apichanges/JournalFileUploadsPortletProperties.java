/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Moved Journal File Uploads Portlet Properties to OSGi Configuration",
		"problem.summary=Moved Journal File Uploads Portlet Properties to OSGi Configuration",
		"problem.tickets=LPS-69209",
		"problem.section=#moved-journal-file-uploads-portlet-properties-to-osgi-configuration",
		"implName=JournalFileUploadsPortletProperties"
	}, 
	service = FileMigrator.class
)
public class JournalFileUploadsPortletProperties 
	extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("journal.image.extensions");
		properties.add("journal.image.small.max.size");
	}

}
