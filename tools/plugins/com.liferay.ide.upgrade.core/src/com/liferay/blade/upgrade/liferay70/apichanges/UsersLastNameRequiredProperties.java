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
		"problem.title=Removed USERS_LAST_NAME_REQUIRED from portal.properties",
		"problem.summary=The USERS_LAST_NAME_REQUIRED property has been removed "
				+ "from portal.properties and the corresponding UI. Required names are now handled "
				+ "on a per-language basis via the language.properties files. It has also been removed as "
				+ "an option from the Portal Settings section of the Control Panel.",
		"problem.tickets=LPS-54956",
		"problem.section=#removed-userslastnamerequired-from-portal-properties-in-favor-of-language-p",
		"implName=UsersLastNameRequiredProperties"
	},
	service = FileMigrator.class
)
public class UsersLastNameRequiredProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> _properties) {
		_properties.add("users.last.name.required");
	}

}
