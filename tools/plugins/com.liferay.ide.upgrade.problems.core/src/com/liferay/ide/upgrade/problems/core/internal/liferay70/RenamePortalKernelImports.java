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

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaImportsMigrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=The portal-kernel and portal-impl folders have many packages with the same name. Therefore, all of these packages are affected by the split package problem",
		"problem.tickets=LPS-61952", "problem.title=Renamed Packages to Fix the Split Packages Problem",
		"problem.section=#renamed-packages-to-fix-the-split-packages-problem", "auto.correct=import", "version=7.0"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class RenamePortalKernelImports extends JavaImportsMigrator {

	public static String[] getFixedImports(String[][] packageChangeMap) {
		String[] newImports = new String[packageChangeMap.length];

		for (int i = 0; i < packageChangeMap.length; i++) {
			newImports[i] = packageChangeMap[i][1];
		}

		return newImports;
	}

	public static String[] getOldImports(String[][] packageChangeMap) {
		String[] oldImports = new String[packageChangeMap.length];

		for (int i = 0; i < packageChangeMap.length; i++) {
			oldImports[i] = packageChangeMap[i][0];
		}

		return oldImports;
	}

	public static String[][] readCSV() {
		try (InputStream in = RenamePortalKernelImports.class.getResourceAsStream(
				"/com/liferay/ide/upgrade/problems/core/internal/liferay70/kernel-rename.csv")) {

			List<String> lines = new ArrayList<>();

			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
				String line;

				while ((line = bufferedReader.readLine()) != null) {
					StringBuffer contents = new StringBuffer(line);

					lines.add(contents.toString());
				}
			}
			catch (Exception e) {
			}

			String[] lineArray = lines.toArray(new String[0]);

			String[][] results = new String[lineArray.length][2];

			for (int i = 0; i < lineArray.length; i++) {
				String line = lineArray[i];

				String[] columns = line.split(",");

				results[i][0] = columns[0];
				results[i][1] = columns[1];
			}

			return results;
		}
		catch (IOException ioe) {
		}

		return null;
	}

	public RenamePortalKernelImports() {
		super(_imports);
	}

	@Override
	public List<FileSearchResult> searchFile(File file, JavaFile javaFile) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		Set<String> importSet = _imports.keySet();

		List<FileSearchResult> importResult = javaFile.findImports(importSet.toArray(new String[0]));

		if (!importResult.isEmpty()) {
			for (FileSearchResult result : importResult) {

				// make sure that our import is not in list of fixed imports

				boolean skip = false;

				if (result.searchContext != null) {
					Collection<String> importValues = _imports.values();

					for (String fixed : importValues.toArray(new String[0])) {
						if (result.searchContext.contains(fixed)) {
							skip = true;

							break;
						}
					}
				}

				if (!skip) {
					result.autoCorrectContext = getPrefix() + _getImportNameFromResult(result);
					searchResults.add(result);
				}
			}
		}

		return _removeDuplicate(searchResults);
	}

	private static Map<String, String> _getImports() {
		Map<String, String> imports = new HashMap<>();

		String[][] csv = readCSV();

		String[] oldImports = getOldImports(csv);
		String[] fixedImports = getFixedImports(csv);

		for (int i = 0; i < oldImports.length; i++) {
			imports.put(oldImports[i], fixedImports[i]);
		}

		return imports;
	}

	private String _getImportNameFromResult(FileSearchResult result) {
		String searchContext = result.searchContext;

		if (searchContext != null) {
			int offSet = result.endOffset - result.startOffset;

			return searchContext.substring(0, offSet);
		}

		return "";
	}

	private List<FileSearchResult> _removeDuplicate(List<FileSearchResult> searchResults) {
		List<FileSearchResult> newList = new ArrayList<>();

		for (FileSearchResult searchResult : searchResults) {
			if (!newList.contains(searchResult)) {
				newList.add(searchResult);
			}
		}

		return newList;
	}

	private static Map<String, String> _imports;

	static {
		_imports = _getImports();
	}

}