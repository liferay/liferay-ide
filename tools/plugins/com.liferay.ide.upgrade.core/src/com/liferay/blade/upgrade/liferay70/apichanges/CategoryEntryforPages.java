
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
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;
import com.liferay.blade.upgrade.liferay70.XMLFileMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=xml",
		"problem.title=Deprecated Category Entry for Pages",
		"problem.summary=The category entry for Site Administration > Pages has been deprecated in favor of Site Administration > Navigation.",
		"problem.tickets=LPS-63667",
		"problem.section=#deprecated-category-entry-for-pages",
		"implName=CategoryEntryforPages"
	},
	service = FileMigrator.class
)
public class CategoryEntryforPages extends XMLFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {

		if (!"liferay-portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		final List<SearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.findElement(
			"control-panel-entry-category", "site_administration.pages"));

		return results;
	}

}
