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
	"file.extensions=java", "problem.title=Adding Tags, Categories, Vocabularies API Changes",
	"problem.summary=The API for adding tags, categories, and vocabularies now requires passing the groupId " +
		"parameter. Previously, it had to be included in the ServiceContext parameter passed to the method.",
	"problem.tickets=LPS-54570",
	"problem.section=#added-required-parameter-groupid-for-adding-tags-categories-and-vocabularie",
	"implName=RequiredParamGroupId", "version=7.0"
},
	service = FileMigrator.class)
public class RequiredParamGroupId extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<>();

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetTagServiceUtil", "addTag", new String[] {"String", "String[]", "ServiceContext"}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetTagLocalServiceUtil", "addTag", new String[] {"long", "String[]", "ServiceContext"}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetCategoryServiceUtil", "addCategory",
				new String[] {
					"long", "java.util.Map<java.util.Locale,java.lang.String>",
					"java.util.Map<java.util.Locale,java.lang.String>", "long", "String[]", "ServiceContext"
				}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetCategoryServiceUtil", "addCategory", new String[] {"String", "long", "ServiceContext"}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "addCategory",
				new String[] {
					"long", "long", "java.util.Map<java.util.Locale,java.lang.String>",
					"java.util.Map<java.util.Locale,java.lang.String>", "long", "String[]", "ServiceContext"
				}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetCategoryLocalServiceUtil", "addCategory",
				new String[] {"long", "String", "long", "ServiceContext"}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetVocabularyServiceUtil", "addVocabulary",
				new String[] {
					"java.util.Map<java.util.Locale,java.lang.String>",
					"java.util.Map<java.util.Locale,java.lang.String>", "String", "ServiceContext"
				}));
		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetVocabularyServiceUtil", "addVocabulary",
				new String[] {
					"String", "java.util.Map<java.util.Locale,java.lang.String>",
					"java.util.Map<java.util.Locale,java.lang.String>", "String", "ServiceContext"
				}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetVocabularyServiceUtil", "addVocabulary", new String[] {"String", "ServiceContext"}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetVocabularyLocalServiceUtil", "addVocabulary",
				new String[] {
					"long", "java.util.Map<java.util.Locale,java.lang.String>",
					"java.util.Map<java.util.Locale,java.lang.String>", "String", "ServiceContext"
				}));
		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetVocabularyLocalServiceUtil", "addVocabulary",
				new String[] {
					"long", "String", "java.util.Map<java.util.Locale,java.lang.String>",
					"java.util.Map<java.util.Locale,java.lang.String>", "String", "ServiceContext"
				}));
		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "AssetVocabularyLocalServiceUtil", "addVocabulary",
				new String[] {"long", "String", "ServiceContext"}));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "JournalFolderServiceUtil", "updateFolder",
				new String[] {"long", "long", "String", "String", "boolean", "ServiceContext"}));
		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "JournalFolderLocalServiceUtil", "updateFolder",
				new String[] {"long", "long", "long", "String", "String", "boolean", "ServiceContext"}));

		return result;
	}

}