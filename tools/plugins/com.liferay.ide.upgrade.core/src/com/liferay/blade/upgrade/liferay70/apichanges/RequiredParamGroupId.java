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
			"file.extensions=java",
			"problem.title=Adding Tags, Categories, Vocabularies API Changes",
			"problem.summary=The API for adding tags, categories, and vocabularies now requires passing the groupId parameter. Previously, it had to be included in the ServiceContext parameter passed to the method.",
			"problem.tickets=LPS-54570",
			"problem.section=#added-required-parameter-groupid-for-adding-tags-categories-and-vocabularie",
			"implName=RequiredParamGroupId"
		},
		service = FileMigrator.class
	)

public class RequiredParamGroupId  extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<SearchResult> result = new ArrayList<SearchResult>();

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetTagServiceUtil", "addTag",
				new String[] { "String", "String[]", "ServiceContext" }));

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetTagLocalServiceUtil", "addTag",
				new String[] { "long", "String[]", "ServiceContext" }));

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetCategoryServiceUtil", "addCategory",
				new String[] { "long", "java.util.Map<java.util.Locale,java.lang.String>",
						"java.util.Map<java.util.Locale,java.lang.String>", "long", "String[]",
						"ServiceContext" }));

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetCategoryServiceUtil", "addCategory",
				new String[] { "String", "long", "ServiceContext" }));

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetCategoryLocalServiceUtil", "addCategory",
				new String[] { "long", "long", "java.util.Map<java.util.Locale,java.lang.String>",
						"java.util.Map<java.util.Locale,java.lang.String>", "long", "String[]",
						"ServiceContext" }));

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetCategoryLocalServiceUtil", "addCategory",
				new String[] { "long", "String", "long", "ServiceContext" }));

		result.addAll(
				javaFileChecker.findMethodInvocations(null,
						"AssetVocabularyServiceUtil", "addVocabulary",
						new String[] { "java.util.Map<java.util.Locale,java.lang.String>",
								"java.util.Map<java.util.Locale,java.lang.String>", "String",
								"ServiceContext" }));
		result.addAll(
				javaFileChecker.findMethodInvocations(null,
						"AssetVocabularyServiceUtil", "addVocabulary",
						new String[] { "String", "java.util.Map<java.util.Locale,java.lang.String>",
								"java.util.Map<java.util.Locale,java.lang.String>", "String",
								"ServiceContext" }));

		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetVocabularyServiceUtil", "addVocabulary",
				new String[] { "String", "ServiceContext" }));

		result.addAll(
				javaFileChecker.findMethodInvocations(null,
						"AssetVocabularyLocalServiceUtil", "addVocabulary",
						new String[] { "long", "java.util.Map<java.util.Locale,java.lang.String>",
								"java.util.Map<java.util.Locale,java.lang.String>", "String",
								"ServiceContext" }));
		result.addAll(
				javaFileChecker.findMethodInvocations(null,
						"AssetVocabularyLocalServiceUtil", "addVocabulary",
						new String[] { "long", "String", "java.util.Map<java.util.Locale,java.lang.String>",
								"java.util.Map<java.util.Locale,java.lang.String>", "String",
								"ServiceContext" }));
		result.addAll(javaFileChecker.findMethodInvocations(null,
				"AssetVocabularyLocalServiceUtil", "addVocabulary",
				new String[] { "long", "String", "ServiceContext" }));

		result.addAll(
				javaFileChecker
						.findMethodInvocations(null, "JournalFolderServiceUtil",
								"updateFolder",
								new String[] { "long", "long", "String",
										"String", "boolean",
										"ServiceContext" }));
		result.addAll(
				javaFileChecker
						.findMethodInvocations(null,
								"JournalFolderLocalServiceUtil", "updateFolder",
								new String[] { "long", "long", "long", "String",
										"String", "boolean",
										"ServiceContext" }));
		return result;
	}
}
