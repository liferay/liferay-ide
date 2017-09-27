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
		"problem.title=Breadcrumb Portlet's Display Styles Changes",
		"problem.summary=Replaced the Breadcrumb Portlet's Display Styles with ADTs",
		"problem.tickets=LPS-53577",
		"problem.section=#replaced-the-breadcrumb-portlets-display-styles-with-adts",
		"implName=BreadcrumbProperties"
	},
	service = FileMigrator.class
)
public class BreadcrumbProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("breadcrumb.display.style.default");
		properties.add("breadcrumb.display.style.options");
	}

}