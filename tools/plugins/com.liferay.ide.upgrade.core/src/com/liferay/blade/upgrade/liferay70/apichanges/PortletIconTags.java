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
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Removed the Tags that Start with portlet:icon-",
		"problem.section=#removed-the-tags-that-start-with-portleticon-",
		"problem.summary=Removed the Tags that Start with portlet:icon-",
		"problem.tickets=LPS-54620",
		"implName=PortletIconTags"
	},
	service = FileMigrator.class
)
public class PortletIconTags extends JSPFileMigrator {

	public PortletIconTags() {
		super(new String[0], new String[0], new String[0], new String[0], _tagNames, new String[0]);
	}

	private static final String[] _tagNames = new String[] {
		"liferay-portlet:icon-close", "liferay-portlet:icon-configuration", "liferay-portlet:icon-edit",
		"liferay-portlet:icon-edit-defaults", "liferay-portlet:icon-edit-guest",
		"liferay-portlet:icon-export-import", "liferay-portlet:icon-help", "liferay-portlet:icon-maximize",
		"liferay-portlet:icon-minimize", "liferay-portlet:icon-portlet-css", "liferay-portlet:icon-print",
		"liferay-portlet:icon-refresh", "liferay-portlet:icon-staging"
	};

}