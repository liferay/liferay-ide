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
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Changed Usage of the liferay-ui:ddm-template-selector Tag",
		"problem.section=#changed-usage-of-the-liferay-uiddm-template-selector-tag",
		"problem.summary=The attribute classNameId of the liferay-ui:ddm-template-selector taglib tag has been renamed className",
		"problem.tickets=LPS-53790",
		"implName=DdmTemplateSelectorTags"
	},
	service = FileMigrator.class
)
public class DdmTemplateSelectorTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file,
			JSPFile jspFileChecker) {

		return jspFileChecker.findJSPTags("liferay-ui:ddm-template-selector", new String[]{"classNameId"} );
	}
}