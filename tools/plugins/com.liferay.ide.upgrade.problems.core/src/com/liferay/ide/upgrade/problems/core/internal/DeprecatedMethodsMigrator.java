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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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

	public DeprecatedMethodsMigrator(JSONArray[] deprecatedMethods, String version) {
		_deprecatedMethods = deprecatedMethods;
		_version = version;
	}

	@Override
	public List<UpgradeProblem> analyze(File file) {
		List<UpgradeProblem> problems = new ArrayList<>();
		String fileExtension = new Path(
			file.getAbsolutePath()
		).getFileExtension();

		for (JSONArray jsonArray : _deprecatedMethods) {
			for (int j = 0; j < jsonArray.length(); j++) {
				try {
					_searchMethod = jsonArray.getJSONObject(j);

					List<FileSearchResult> searchResults = searchFile(
						file, createFileService(type, file, fileExtension));

					if (searchResults != null) {
						for (FileSearchResult searchResult : searchResults) {
							int makerType = UpgradeProblem.MARKER_ERROR;

							if (Objects.equals(problemVersion, _searchMethod.getString("deprecatedVersion"))) {
								makerType = UpgradeProblem.MARKER_WARNING;
							}

							problems.add(
								new UpgradeProblem(
									_searchMethod.getString("javadoc"), _searchMethod.getString("javadoc"),
									fileExtension, "", _version, file, searchResult.startLine, searchResult.startOffset,
									searchResult.endOffset, _searchMethod.getString("javadoc"),
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

	protected static String read(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}

		char[] buffer = new char[0x10000];

		StringBuilder out = new StringBuilder();

		try (Reader in = new InputStreamReader(inputStream, "UTF-8")) {
			int read;

			do {
				read = in.read(buffer, 0, buffer.length);

				if (read > 0) {
					out.append(buffer, 0, read);
				}
			}
			while (read >= 0);
		}

		inputStream.close();

		return out.toString();
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, JavaFile javaFile) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		String[] parameters = null;

		try {
			JSONArray parameterJSONArray = _searchMethod.getJSONArray("parameters");

			if (parameterJSONArray != null) {
				parameters = new String[parameterJSONArray.length()];

				for (int i = 0; i < parameterJSONArray.length(); i++) {
					parameters[i] = parameterJSONArray.getString(i);
				}
			}

			searchResults.addAll(
				javaFile.findMethodInvocations(
					_searchMethod.getString("className"), null, _searchMethod.getString("methodName"), parameters));

			searchResults.addAll(
				javaFile.findMethodInvocations(
					null, _searchMethod.getString("className"), _searchMethod.getString("methodName"), parameters));
		}
		catch (JSONException jsone) {
		}

		return searchResults;
	}

	private JSONArray[] _deprecatedMethods;
	private JSONObject _searchMethod = null;
	private String _version;

}