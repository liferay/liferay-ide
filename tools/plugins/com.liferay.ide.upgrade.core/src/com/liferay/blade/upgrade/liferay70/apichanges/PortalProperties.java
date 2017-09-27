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
		"problem.title=Portal Property Changes",
		"problem.summary=Removed Portal Properties Used to Display Sections in Form Navigators",
		"problem.tickets=LPS-54903",
		"problem.section=#removed-portal-properties-used-to-display-sections-in-form-navigators",
		"implName=PortalProperties"
	},
	service = FileMigrator.class
)
public class PortalProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("company.settings.form.configuration");
		properties.add("company.settings.form.identification");
		properties.add("company.settings.form.miscellaneous");
		properties.add("company.settings.form.social");
		properties.add("layout.form.add");
		properties.add("layout.form.update");
		properties.add("layout.set.form.update");
		properties.add("organizations.form.add.identification");
		properties.add("organizations.form.add.main");
		properties.add("organizations.form.add.miscellaneous");
		properties.add("organizations.form.update.identification");
		properties.add("organizations.form.update.main");
		properties.add("organizations.form.update.miscellaneous");
		properties.add("sites.form.add.advanced");
		properties.add("sites.form.add.main");
		properties.add("sites.form.add.miscellaneous");
		properties.add("sites.form.add.seo");
		properties.add("sites.form.update.advanced");
		properties.add("sites.form.update.main");
		properties.add("sites.form.update.miscellaneous");
		properties.add("sites.form.update.seo");
		properties.add("users.form.add.identification");
		properties.add("users.form.add.main");
		properties.add("users.form.add.miscellaneous");
		properties.add("users.form.my.account.identification");
		properties.add("users.form.my.account.main");
		properties.add("users.form.my.account.miscellaneous");
		properties.add("users.form.update.identification");
		properties.add("users.form.update.main");
		properties.add("users.form.update.miscellaneous");
	}

}