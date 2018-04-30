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

package com.liferay.blade.upgrade.liferay70.deprecatedmethods;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;

import org.json.JSONArray;
import org.json.JSONObject;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {"file.extensions=java,jsp,jspf", "implName=DeprecatedMethodsMigrator"}, service = FileMigrator.class
)
public class DeprecatedMethodsMigrator extends JavaFileMigrator {

	public static JSONArray[] getDeprecatedMethods() {
		if (_deprecatedMethods == null) {
			List<JSONArray> deprecatedMethodsList = new ArrayList<>();

			String fqn = "/com/liferay/blade/upgrade/liferay70/deprecatedmethods/";

			String[] jsonFilePaths = {
				fqn + "deprecatedMethods62.json", fqn + "deprecatedMethods61.json",
				fqn + "deprecatedMethodsNoneVersionFile.json"
			};

			Class<? extends DeprecatedMethodsMigrator> class1 = DeprecatedMethodsMigrator.class;

			for (int i = 0; i < jsonFilePaths.length; i++) {
				try{
					String jsonContent = CoreUtil.readStreamToString(class1.getResourceAsStream(jsonFilePaths[i]));

					deprecatedMethodsList.add(new JSONArray(jsonContent));
				}
				catch (IOException ioe) {
				}
			}

			_deprecatedMethods = deprecatedMethodsList.toArray(new JSONArray[0]);
		}

		return _deprecatedMethods;
	}

	@Override
	public List<Problem> analyze(File file) {
		List<Problem> problems = new ArrayList<>();
		String fileExtension = new Path(file.getAbsolutePath()).getFileExtension();

		for (JSONArray deprecatedMethodsArray : _deprecatedMethods) {
			for (int j = 0; j < deprecatedMethodsArray.length(); j++) {
				_tempMethod = deprecatedMethodsArray.getJSONObject(j);

				List<SearchResult> searchResults = searchFile(file, createFileChecker(type, file, fileExtension));

				if (searchResults != null) {
					for (SearchResult searchResult : searchResults) {
						int makerType = Problem.MARKER_ERROR;

						if (_tempMethod.getString("deprecatedVersion").equals("7.0")) {
							makerType = Problem.MARKER_WARNING;
						}

						problems.add(
							new Problem(
								_tempMethod.getString("javadoc"), _tempMethod.getString("javadoc"), fileExtension, "",
								file, searchResult.startLine, searchResult.startOffset, searchResult.endOffset,
								_tempMethod.getString("javadoc"), searchResult.autoCorrectContext,
								Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, makerType));
					}
				}
			}
		}

		return problems;
	}

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile fileChecker) {
		List<SearchResult> searchResults = new ArrayList<>();

		String[] parameters = null;

		JSONArray parameterJSONArray = _tempMethod.getJSONArray("parameters");

		if (parameterJSONArray != null) {
			parameters = new String[parameterJSONArray.length()];

			for (int i = 0; i < parameterJSONArray.length(); i++) {
				parameters[i] = parameterJSONArray.getString(i);
			}
		}

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				_tempMethod.getString("className"), null, _tempMethod.getString("methodName"), parameters));

		searchResults.addAll(
			fileChecker.findMethodInvocations(
				null, _tempMethod.getString("className"), _tempMethod.getString("methodName"), parameters));

		return searchResults;
	}

	private static JSONArray[] _deprecatedMethods;

	static {
		_deprecatedMethods = getDeprecatedMethods();
	}

	private JSONObject _tempMethod = null;

}