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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.title=Web Content Articles Now Require a Structure and Template",
		"problem.summary=Web content is now required to use a structure and template. A default structure and template named Basic Web Content was added to the global scope, and can be modified or deleted.",
		"problem.tickets=LPS-45107", "problem.section=#web-content-articles-now-require-a-structure-and-template",
		"version=7.0"
	},
	service = FileMigrator.class
)
public class WebContentArticlesStrucAndTempl extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {

		// Journal API to create web content without a structure
		// or template are affected

		List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> journalArticleUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "addArticle", null);

		searchResults.addAll(journalArticleUtil);

		journalArticleUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "addArticle", null);

		searchResults.addAll(journalArticleUtil);

		return searchResults;
	}

}