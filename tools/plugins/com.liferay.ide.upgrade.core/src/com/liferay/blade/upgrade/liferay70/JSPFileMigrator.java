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

package com.liferay.blade.upgrade.liferay70;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JSPFileMigrator extends AbstractFileMigrator<JSPFile> implements AutoMigrator {

	private final String[] _attrNames;
	private final String[] _newAttrNames;
	private final String[] _attrValues;
	private final String[] _newAttrValues;
	private final String[] _tagNames;
	private final String[] _newTagNames;

	public JSPFileMigrator(String[] attrNames, String[] newAttrNames, String[] attrValues,
			String[] newAttrValues, String[] tagNames, String[] newTagNames) {

		super(JSPFile.class);

		_attrNames = attrNames;
		_newAttrNames = newAttrNames;
		_attrValues = attrValues;
		_newAttrValues = newAttrValues;
		_tagNames = tagNames;
		_newTagNames = newTagNames;
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int problemsFixed = 0;
		Map<Integer,String[]> tagsToRewrite = new HashMap<>();

		for (Problem problem : problems) {
			if (problem.autoCorrectContext instanceof String) {
				String context = problem.autoCorrectContext;

				if (context.equals("jsptag:" + getClass().getName())) {
					if (_newAttrValues.length > 0) {
						tagsToRewrite.put(problem.getLineNumber(), _attrValues);
						problemsFixed++;
					}
					else if (_newAttrNames.length > 0) {
						tagsToRewrite.put(problem.getLineNumber(), _attrNames);
						problemsFixed++;
					}
					else if (_newTagNames.length > 0) {
						tagsToRewrite.put(problem.getLineNumber(), _tagNames);
						problemsFixed++;
					}
				}
			}
		}

		if (tagsToRewrite.size() > 0) {
			try  {
				FileInputStream in = new FileInputStream(file);
				String[] lines = readLines(in);
				in.close();

				String[] editedLines = new String[lines.length];
				System.arraycopy(lines, 0, editedLines, 0, lines.length);

				for (int lineNumber : tagsToRewrite.keySet()) {
					String[] oldValues = tagsToRewrite.get(lineNumber);
					boolean tagNameFix = false;

					for (int i = 0; i < oldValues.length; i++) {
						String oldValue = oldValues[i];
						String newValue = "";

						if (_newAttrValues.length > 0) {
							newValue = _newAttrValues[i];
						}
						else if (_newAttrNames.length > 0) {
							newValue = _newAttrNames[i];
						}
						else if (_newTagNames.length > 0) {
							newValue = _newTagNames[i];

							tagNameFix = true;
						}

						if (!newValue.equals("")) {
							editedLines[lineNumber - 1] = editedLines[lineNumber - 1].replaceAll(oldValue, newValue);

							if (tagNameFix) {
								String oldEndTag = "</" + oldValue + ">";
								String newEndTag = "</" + newValue + ">";

								for (int t = lineNumber - 1; t < editedLines.length; t++) {
									if (editedLines[t].contains(oldEndTag)) {
										editedLines[t] = editedLines[t].replaceAll(oldEndTag, newEndTag);
									}
								}
							}
						}
					}
				}

				StringBuilder sb = new StringBuilder();

				for (String editedLine : editedLines) {
					sb.append(editedLine);
					sb.append(System.getProperty("line.separator"));
				}

				FileWriter writer = new FileWriter(file);
				writer.write(sb.toString());
				writer.close();

				return problemsFixed;
			}
			catch (IOException e) {
				throw new AutoMigrateException("Unable to auto-correct", e);
			}
		}

		return 0;
	}

	private static String[] readLines(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (BufferedReader bufferedReader =
				new BufferedReader(new InputStreamReader(inputStream))) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return lines.toArray(new String[lines.size()]);
	}

	@Override
	protected List<SearchResult> searchFile(File file, JSPFile jspFileChecker) {
		List<SearchResult> searchResults = new ArrayList<SearchResult>();

		for (String tagName : _tagNames) {
			List<SearchResult> jspTagResults = new ArrayList<SearchResult>();

			if ((_tagNames.length > 0) && (_attrNames.length == 0) && (_attrValues.length == 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName);
			}
			else if ((_tagNames.length > 0) && (_attrNames.length > 0) && (_attrValues.length == 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName, _attrNames);
			}
			else if ((_tagNames.length > 0) && (_attrNames.length > 0) && (_attrValues.length > 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName, _attrNames, _attrValues);
			}

			if (!jspTagResults.isEmpty()) {
				searchResults.addAll(jspTagResults);
			}
		}

		if (_newAttrNames.length > 0 || _newAttrValues.length > 0 || _newTagNames.length > 0) {
			for (SearchResult searchResult : searchResults) {
				searchResult.autoCorrectContext = "jsptag:" + getClass().getName();
			}
		}

		return searchResults;
	}

}
