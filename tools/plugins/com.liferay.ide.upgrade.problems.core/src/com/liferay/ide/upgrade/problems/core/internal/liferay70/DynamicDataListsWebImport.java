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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Joye Luo
 */
@Component(property = {
	"file.extensions=java,jsp,jspf", "problem.title=Removed Exports from Dynamic Data Lists Web",
	"problem.summary=The `Dynamic Data Lists Web` module no longer exports the `com.liferay.dynamic.data.lists.web.asset` package.",
	"problem.tickets=LPS-75778", "problem.section=#removed-exports-from-dynamic-data-lists-web",
	 "version=7.0"
},
	service = FileMigrator.class)
public class DynamicDataListsWebImport extends JavaFileMigrator {

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile fileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		searchResults.add(fileChecker.findImport("com.liferay.dynamic.data.lists.web.asset.DDLRecordAssetRenderer"));
		searchResults.add(
			fileChecker.findImport("com.liferay.dynamic.data.lists.web.asset.DDLRecordAssetRendererFactory"));
		searchResults.add(
			fileChecker.findImport("com.liferay.dynamic.data.lists.web.asset.DDLRecordDDMFormValuesReader"));
		searchResults.add(fileChecker.findImport("com.liferay.dynamic.data.lists.web.asset.DDLRecordSetClassType"));
		searchResults.add(
			fileChecker.findImport("com.liferay.dynamic.data.lists.web.asset.DDLRecordSetClassTypeReader"));

		return searchResults;
	}

}