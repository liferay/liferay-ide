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
		"problem.title=User Operation and Importer/Exporter Classes and Utilities Moved or Removed",
		"problem.section=#user-operation-and-importer-exporter-classes-and-utilities-have-been-moved-",
		"problem.summary==User Operation and Importer/Exporter Classes and Utilities Moved or Removed",
		"problem.tickets=LPS-63205",
		"implName=UserImporterExporter"
	},
	service = FileMigrator.class
)
public class UserImporterExporter extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {

		final List<SearchResult> searchResults = new ArrayList<>();

		SearchResult searchResult = javaFileChecker.findImport(
				"com.liferay.portal.kernel.security.exportimport.UserImporter");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport(
				"com.liferay.portal.kernel.security.exportimport.UserExporter");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport(
				"com.liferay.portal.kernel.security.exportimport.UserOperation");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport(
				"com.liferay.portal.kernel.security.exportimport.UserImporterUtil");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport(
				"com.liferay.portal.kernel.security.exportimport.UserExporterUtil");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		return searchResults;
	}
}
