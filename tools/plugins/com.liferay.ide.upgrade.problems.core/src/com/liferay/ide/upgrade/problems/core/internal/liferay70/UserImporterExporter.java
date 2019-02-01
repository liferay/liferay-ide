/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import com.liferay.ide.upgrade.plan.tasks.core.SearchResult;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java,jsp,jspf",
	"problem.title=User Operation and Importer/Exporter Classes and Utilities Moved or Removed",
	"problem.section=#user-operation-and-importer-exporter-classes-and-utilities-have-been-moved-",
	"problem.summary==User Operation and Importer/Exporter Classes and Utilities Moved or Removed",
	"problem.tickets=LPS-63205", "implName=UserImporterExporter", "version=7.0"
},
	service = FileMigrator.class)
public class UserImporterExporter extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		SearchResult searchResult = javaFileChecker.findImport(
			"com.liferay.portal.kernel.security.exportimport.UserImporter");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport("com.liferay.portal.kernel.security.exportimport.UserExporter");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport("com.liferay.portal.kernel.security.exportimport.UserOperation");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport("com.liferay.portal.kernel.security.exportimport.UserImporterUtil");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		searchResult = javaFileChecker.findImport("com.liferay.portal.kernel.security.exportimport.UserExporterUtil");

		if (searchResult != null) {
			searchResults.add(searchResult);
		}

		return searchResults;
	}

}