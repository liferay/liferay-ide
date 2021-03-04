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

package com.liferay.ide.upgrade.problems.core.internal.liferay73;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.summary=Removed java class AssetTagsSelectorTag",
		"problem.tickets=LPS-100144", "problem.title=Removed java class AssetTagsSelectorTag",
		"problem.section=#removed-soy-assettagselectortag", "version=7.3"
	},
	service = FileMigrator.class
)
public class RemoveAssetTagsSelectorTag extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		FileSearchResult searchResult = javaFileChecker.findImport(
			"com.liferay.asset.taglib.servlet.taglib.soy.AssetTagsSelectorTag");

		if (searchResult != null) {
			results.add(searchResult);
		}

		results.addAll(javaFileChecker.findSuperClass("AssetTagsSelectorTag"));

		return results;
	}

}