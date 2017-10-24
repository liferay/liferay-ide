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

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Removed mbMessages and fileEntryTuples Attributes from app-view-search-entry Tag",
		"problem.section=#removed-mbmessages-and-fileentrytuples-attributes-from-app-view-search-entr",
		"problem.summary=Removed mbMessages and fileEntryTuples Attributes from app-view-search-entry Tag",
		"problem.tickets=LPS-55886",
		"implName=AppViewSearchEntryTags"
	},
	service = FileMigrator.class
)
public class AppViewSearchEntryTags extends JSPFileMigrator {

	@Override
	protected String[] getTagNames() {
		return new String[] {
			"liferay-ui:app-view-search-entry"
		};
	}

	@Override
	protected String[] getAttrNames() {
		return new String[] {
			"mbMessages", "fileEntryTuples"
		};
	}

}