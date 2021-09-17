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

package com.liferay.ide.upgrade.problems.core.internal.liferay74;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Simon Jiang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.title=Removed JournalArticle Content Field",
		"problem.summary=JournalArticle content is now stored by DDM Field services.", "problem.tickets=LPS-129058",
		"problem.section=#remove-journalarticle-content-field", "version=7.4"
	},
	service = FileMigrator.class
)
public class RemoveJournalArticleContentField extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleModel", null, "getContent", null));
		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleModel", null, "setContent", null));

		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleSoap", null, "getContent", null));
		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleSoap", null, "setContent", null));

		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleWrapper", null, "setContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleLocalService", null, "checkNewLine", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleLocalService", null, "updateContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "JournalArticleLocalServiceUtil", "checkNewLine", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "JournalArticleLocalServiceUtil", "updateContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleLocalServiceWrapper", null, "checkNewLine", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleLocalServiceWrapper", null, "updateContent", null));

		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleService", null, "updateContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "JournalArticleServiceUtil", "updateContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleServiceWrapper", null, "updateContent", null));

		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleImpl", null, "setContent", null));

		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleModelImpl", null, "getContent", null));
		searchResults.addAll(fileChecker.findMethodInvocations("JournalArticleModelImpl", null, "setContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "JournalArticleServiceHttp", "updateContent", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations(null, "JournalArticleServiceSoap", "updateContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleLocalServiceImpl", null, "checkNewLine", null));
		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleLocalServiceImpl", null, "updateContent", null));

		searchResults.addAll(
			fileChecker.findMethodInvocations("JournalArticleServiceImpl", null, "updateContent", null));

		return searchResults;
	}

}