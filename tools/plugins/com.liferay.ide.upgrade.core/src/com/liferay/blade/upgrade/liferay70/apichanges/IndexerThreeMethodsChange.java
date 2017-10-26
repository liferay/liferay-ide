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
		"problem.title=Changes to Indexer methods",
		"problem.summary=Method Indexer.addRelatedEntryFields(Document, Object) has been moved into RelatedEnt" +
			"ryIndexer. Indexer.reindexDDMStructures(List<Long>) has been moved into DDMStructureIndexer." +
			" Indexer.getQueryString(SearchContext, Query) has been removed, in favor of calling SearchEngineUtil." +
			"getQueryString(SearchContext, Query)",
		"problem.tickets=LPS-55928",
		"problem.section=#moved-indexer-addrelatedentryfields-and-indexer-reindexddmstructures-and-re",
		"implName=IndexerThreeMethodsChange"
	},
	service = FileMigrator.class
)
public class IndexerThreeMethodsChange  extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();
		List<SearchResult> declarations = new ArrayList<>();
		List<SearchResult> interfaceSearch = javaFileChecker.findImplementsInterface("RelatedEntryIndexer");

		if (interfaceSearch.isEmpty()) {
			declarations = javaFileChecker.findMethodDeclaration("addRelatedEntryFields",
					new String[] { "Document", "Object" }, null);
			searchResults.addAll(declarations);
		}

		interfaceSearch = javaFileChecker.findImplementsInterface("DDMStructureIndexer");

		if (interfaceSearch.isEmpty()) {
			declarations = javaFileChecker.findMethodDeclaration(
					"reindexDDMStructures", new String[] { "List<Long>" }, null);
			searchResults.addAll(declarations);
		}

		declarations = javaFileChecker.findMethodDeclaration("getQueryString",
				new String[] { "SearchContext", "Query" }, null);
		searchResults.addAll(declarations);

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
				"Indexer", null, "addRelatedEntryFields",
				new String[] { "Document", "Object" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("Indexer", null,
				"reindexDDMStructures", new String[] { "java.util.List<java.lang.Long>" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("Indexer", null,
				"getQueryString", new String[] { "SearchContext", "Query" });
		searchResults.addAll(invocations);

		return searchResults;
	}
}
