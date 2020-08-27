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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class DeprecatedMethodsMigrator extends JavaFileMigrator {

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();
		String fileExtension = new Path(
			file.getAbsolutePath()
		).getFileExtension();

		for (JSONArray deprecatedMethodsArray : _deprecatedMethods) {
			for (int j = 0; j < deprecatedMethodsArray.length(); j++) {
				try {
					_tempMethod = deprecatedMethodsArray.getJSONObject(j);

					List<FileSearchResult> searchResults = searchFile(
						file, createFileService(type, file, fileExtension));

					if (searchResults != null) {
						for (FileSearchResult searchResult : searchResults) {
							int makerType = UpgradeProblem.MARKER_ERROR;

							if (Objects.equals("7.0", _tempMethod.getString("deprecatedVersion"))) {
								makerType = UpgradeProblem.MARKER_WARNING;
							}

							problems.add(
								new UpgradeProblem(
									_tempMethod.getString("javadoc"), _tempMethod.getString("javadoc"), fileExtension,
									"", "7.0", file, searchResult.startLine, searchResult.startOffset,
									searchResult.endOffset, _tempMethod.getString("javadoc"),
									searchResult.autoCorrectContext, UpgradeProblem.STATUS_NOT_RESOLVED,
									UpgradeProblem.DEFAULT_MARKER_ID, makerType));
						}
					}
				}
				catch (JSONException jsone) {
				}
			}
		}

		return problems;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFile) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		String[] parameters = null;

		try {
			JSONArray parameterJSONArray = _tempMethod.getJSONArray("parameters");

			if (parameterJSONArray != null) {
				parameters = new String[parameterJSONArray.length()];

				for (int i = 0; i < parameterJSONArray.length(); i++) {
					parameters[i] = parameterJSONArray.getString(i);
				}
			}

			searchResults.addAll(
				javaFile.findMethodInvocations(
					_tempMethod.getString("className"), null, _tempMethod.getString("methodName"), parameters));

			searchResults.addAll(
				javaFile.findMethodInvocations(
					null, _tempMethod.getString("className"), _tempMethod.getString("methodName"), parameters));
		}
		catch (JSONException jsone) {
		}

		return searchResults;
	}

	protected JSONArray[] _deprecatedMethods;

	private JSONObject _tempMethod = null;

}