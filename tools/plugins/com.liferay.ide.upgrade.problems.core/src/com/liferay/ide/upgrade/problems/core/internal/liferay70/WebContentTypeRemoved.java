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

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=The field type from the Journal Article entity has been removed. ",
		"problem.title=Migration of the Field Type from the Journal Article API into a Vocabulary. The Journal API no longer supports this parameter. A new vocabulary called Web Content Types is created when migrating from previous versions of Liferay, and the types from the existing articles are kept as categories of this vocabulary.",
		"problem.tickets=LPS-50764",
		"problem.section=#migration-of-the-field-type-from-the-journal-article-api-into-a-vocabulary",
		"problem.version=7.0", "version=7.0"
	},
	service = FileMigrator.class
)
public class WebContentTypeRemoved extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		// check JournalArticle.getType() and JournalFeed.getType()

		List<FileSearchResult> getTypes = javaFileChecker.findMethodInvocations(
			"JournalArticle", null, "getType", null);

		searchResults.addAll(getTypes);

		getTypes = javaFileChecker.findMethodInvocations("JournalFeed", null, "getType", null);

		searchResults.addAll(getTypes);

		// callers of ArticleTypeException's methods

		FileSearchResult exceptionImports = javaFileChecker.findImport(
			"com.liferay.portlet.journal.ArticleTypeException");

		if (exceptionImports != null) {
			searchResults.add(exceptionImports);
		}

		List<FileSearchResult> catchExceptions = javaFileChecker.findCatchExceptions(
			new String[] {"ArticleTypeException"});

		searchResults.addAll(catchExceptions);

		// JournalArticleLocalServiceUtil

		List<FileSearchResult> journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "addArticle",
			new String[] {
				"long", "long", "long", "long", "long", "String", "boolean", "double",
				"java.util.Map<java.util.Locale,java.lang.String>", "java.util.Map<java.util.Locale,java.lang.String>",
				"String", "String", "String", "String", "String", "int", "int", "int", "int", "int", "int", "int",
				"int", "int", "int", "boolean", "int", "int", "int", "int", "int", "boolean", "boolean", "boolean",
				"String", "File", "java.util.Map<java.lang.String,byte[]>", "String", "ServiceContext"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "Date", "Date", "int", "Date", "int", "int", "OrderByComparator"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "String", "String", "String", "Date", "Date", "int", "Date", "boolean", "int", "int",
				"OrderByComparator"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "String", "String[]", "String[]", "Date", "Date", "int", "Date", "boolean", "int", "int",
				"OrderByComparator"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<Long>", "long", "String", "String", "String", "String", "String",
				"String", "String", "String", "LinkedHashMap<String,Object>", "boolean", "int", "int", "Sort"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "searchCount",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "Date", "Date", "int", "Date"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "searchCount",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "String", "String", "String", "Date", "Date", "int", "Date", "boolean"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "searchCount",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "String", "String[]", "String[]", "Date", "Date", "int", "Date", "boolean"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		journalArticleLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleLocalServiceUtil", "updateArticle",
			new String[] {
				"long", "long", "long", "String", "double", "java.util.Map<java.util.Locale,java.lang.String>",
				"java.util.Map<java.util.Locale,java.lang.String>", "String", "String", "String", "String", "String",
				"int", "int", "int", "int", "int", "int", "int", "int", "int", "int", "boolean", "int", "int", "int",
				"int", "int", "boolean", "boolean", "boolean", "String", "File",
				"java.util.Map<java.lang.String,byte[]>", "String", "ServiceContext"
			});

		searchResults.addAll(journalArticleLocalServiceUtil);

		// JournalArticleServiceUtil

		List<FileSearchResult> journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "addArticle",
			new String[] {
				"long", "long", "long", "long", "String", "boolean", "java.util.Map<java.util.Locale,java.lang.String>",
				"java.util.Map<java.util.Locale,java.lang.String>", "String", "String", "String", "String", "String",
				"int", "int", "int", "int", "int", "int", "int", "int", "int", "int", "boolean", "int", "int", "int",
				"int", "int", "boolean", "boolean", "boolean", "String", "File",
				"java.util.Map<java.lang.String,byte[]>", "String", "ServiceContext"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "addArticle",
			new String[] {
				"long", "long", "long", "long", "String", "boolean", "java.util.Map<java.util.Locale,java.lang.String>",
				"java.util.Map<java.util.Locale,java.lang.String>", "String", "String", "String", "String", "String",
				"int", "int", "int", "int", "int", "int", "int", "int", "int", "int", "boolean", "int", "int", "int",
				"int", "int", "boolean", "boolean", "String", "ServiceContext"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<Long>", "long", "String", "Double", "String", "String", "String",
				"Date", "Date", "int", "Date", "int", "int", "OrderByComparator"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "String", "String", "String", "Date", "Date", "int", "Date", "boolean", "int", "int",
				"OrderByComparator"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "search",
			new String[] {
				"long", "long", "java.util.List<java.lang.Long>", "long", "String", "Double", "String", "String",
				"String", "String", "String[]", "String[]", "Date", "Date", "int", "Date", "boolean", "int", "int",
				"OrderByComparator"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "searchCount",
			new String[] {
				"long", "long", "java.util.List<Long>", "long", "String", "Double", "String", "String", "String",
				"Date", "Date", "int", "Date"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "searchCount",
			new String[] {
				"long", "long", "java.util.List<Long>", "long", "String", "Double", "String", "String", "String",
				"String", "String", "String", "Date", "Date", "int", "Date", "boolean"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "searchCount",
			new String[] {
				"long", "long", "java.util.List<Long>", "long", "String", "Double", "String", "String", "String",
				"String", "String[]", "String[]", "Date", "Date", "int", "Date", "boolean"
			});

		searchResults.addAll(journalArticleServiceUtil);

		journalArticleServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalArticleServiceUtil", "updateArticle",
			new String[] {
				"long", "long", "String", "double", "java.util.Map<java.util.Locale,java.lang.String>",
				"java.util.Map<java.util.Locale,java.lang.String>", "String", "String", "String", "String", "String",
				"int", "int", "int", "int", "int", "int", "int", "int", "int", "int", "boolean", "int", "int", "int",
				"int", "int", "boolean", "boolean", "boolean", "String", "File",
				"java.util.Map<java.lang.String,byte[]>", "String", "ServiceContext"
			});

		searchResults.addAll(journalArticleServiceUtil);

		// JournalFeedLocalServiceUtil

		List<FileSearchResult> journalFeedLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalFeedLocalServiceUtil", "addFeed",
			new String[] {
				"long", "long", "String", "boolean", "String", "String", "String", "String", "String", "String", "int",
				"String", "String", "String", "String", "String", "String", "double", "ServiceContext"
			});

		searchResults.addAll(journalFeedLocalServiceUtil);

		journalFeedLocalServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalFeedLocalServiceUtil", "updateFeed",
			new String[] {
				"long", "String", "String", "String", "String", "String", "String", "String", "int", "String", "String",
				"String", "String", "String", "String", "double", "ServiceContext"
			});

		searchResults.addAll(journalFeedLocalServiceUtil);

		// JournalFeedServiceUtil

		List<FileSearchResult> journalFeedServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalFeedServiceUtil", "addFeed",
			new String[] {
				"long", "String", "boolean", "String", "String", "String", "String", "String", "String", "int",
				"String", "String", "String", "String", "String", "String", "double", "ServiceContext"
			});

		searchResults.addAll(journalFeedServiceUtil);

		journalFeedServiceUtil = javaFileChecker.findMethodInvocations(
			null, "JournalFeedServiceUtil", "updateFeed",
			new String[] {
				"long", "String", "String", "String", "String", "String", "String", "String", "int", "String", "String",
				"String", "String", "String", "String", "double", "ServiceContext"
			});

		searchResults.addAll(journalFeedServiceUtil);

		return searchResults;
	}

}