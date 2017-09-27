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
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=Replaced Method getFacetQuery with getFacetBooleanFilter in Indexer",
		"problem.tickets=LPS-56064",
		"problem.title=Indexer API Changes",
		"problem.section=#replaced-method-getpermissionquery-with-getpermissionfilter-in-searchpermis",
		"implName=IndexerGetFacetQuery"
	},
	service = FileMigrator.class
)
public class IndexerGetFacetQuery extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		final List<SearchResult> declaration = javaFileChecker.findMethodDeclaration("getFacetQuery",
				new String[] { "String", "SearchContextPortletURL" }, null);

		searchResults.addAll(declaration);

		final List<SearchResult> invocations = javaFileChecker.findMethodInvocations("Indexer", null, "getFacetQuery",
				null);

		searchResults.addAll(invocations);

		return searchResults;
	}
}