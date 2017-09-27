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
		"problem.title=Web Content Articles Now Require a Structure and Template",
		"problem.summary=Web content is now required to use a structure and template. " +
			"A default structure and template named Basic Web Content was " +
			"added to the global scope, and can be modified or deleted.",
		"problem.tickets=LPS-45107",
		"problem.section=#web-content-articles-now-require-a-structure-and-template",
		"implName=WebContentArticlesStrucAndTempl"
	},
	service = FileMigrator.class
)
public class WebContentArticlesStrucAndTempl extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		// Journal API to create web content without a structure
		// or template are affected
		final List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> journalArticleUtil = javaFileChecker
				.findMethodInvocations(null, "JournalArticleLocalServiceUtil",
						"addArticle", null);

		searchResults.addAll(journalArticleUtil);

		journalArticleUtil = javaFileChecker.findMethodInvocations(null,
				"JournalArticleServiceUtil", "addArticle", null);

		searchResults.addAll(journalArticleUtil);

		return searchResults;
	}

}
