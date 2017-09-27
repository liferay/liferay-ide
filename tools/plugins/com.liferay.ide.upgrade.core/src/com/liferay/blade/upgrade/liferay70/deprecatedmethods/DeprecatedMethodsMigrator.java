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

@Component(
		property = {
			"file.extensions=java,jsp,jspf",
			"implName=DeprecatedMethodsMigrator"
		},
		service = FileMigrator.class
	)
public class DeprecatedMethodsMigrator extends JavaFileMigrator {

	private static JSONObject tempMethod = null;

	@Override
	public List<Problem> analyze(File file) {
		final List<Problem> problems = new ArrayList<>();

		JSONArray[] deprecatedMethods = getDeprecatedMethods();

		for (int i = 0; i < deprecatedMethods.length; i++) {
			JSONArray deprecatedMethodsArray = deprecatedMethods[i];

			for (int j = 0; j < deprecatedMethodsArray.length(); j++) {
				tempMethod = deprecatedMethodsArray.getJSONObject(j);

				final List<SearchResult> searchResults = searchFile(file, createFileChecker(_type, file));

				if (searchResults != null) {
					for (SearchResult searchResult : searchResults) {
						String fileExtension = new Path(file.getAbsolutePath()).getFileExtension();

						int makerType = Problem.MARKER_ERROR;

						if (tempMethod.getString("deprecatedVersion").equals("7.0")) {
							makerType = Problem.MARKER_WARNING;
						}

						problems.add(new Problem(tempMethod.getString("javadoc"), tempMethod.getString("javadoc"),
								fileExtension, "", file, searchResult.startLine, searchResult.startOffset,
								searchResult.endOffset, tempMethod.getString("javadoc"), searchResult.autoCorrectContext,
								Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, makerType));
					}
				}
			}
		}

		return problems;
	}

	public JSONArray[] getDeprecatedMethods() {
		String fqn = "/com/liferay/blade/upgrade/liferay70/deprecatedmethods/";

		String[] jsonFilePaths = new String[] {
			fqn + "deprecatedMethods62.json",
			fqn + "deprecatedMethods61.json",
			fqn + "deprecatedMethodsNoneVersionFile.json"
		};

		JSONArray[] retval = new JSONArray[jsonFilePaths.length];

		for (int i = 0; i < jsonFilePaths.length; i++) {
			try (InputStream in = getClass().getResourceAsStream(jsonFilePaths[i])) {
				String jsonContent = CoreUtil.readStreamToString(in);
				retval[i] = new JSONArray(jsonContent);
			} catch (IOException e) {
			}
		}

		return retval;
	}

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile fileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		String[] parameters = null;

		JSONArray parameterJSONArray = tempMethod.getJSONArray("parameters");

		if (parameterJSONArray != null) {
			parameters = new String[parameterJSONArray.length()];

			for (int i = 0; i < parameterJSONArray.length(); i++) {
				parameters[i] = parameterJSONArray.getString(i);
			}
		}

		searchResults.addAll(fileChecker.findMethodInvocations(
			tempMethod.getString("className"), null, tempMethod.getString("methodName"), parameters));

		searchResults.addAll(fileChecker.findMethodInvocations(
			null, tempMethod.getString("className"), tempMethod.getString("methodName"), parameters));

		return searchResults;
	}

}