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

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;

public abstract class JSPFileMigrator extends AbstractFileMigrator<JSPFile> implements AutoMigrator{

	public JSPFileMigrator() {
		super(JSPFile.class);
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int problemsFixed = 0;
		Map<Integer,String[]> tagsToRewrite = new HashMap<>();

		for (Problem problem : problems) {
			if (problem.autoCorrectContext instanceof String) {
				String context = problem.autoCorrectContext;

				if (context != null && context.equals("jsptag:" + getClass().getName())) {
					if (getNewAttrValues().length > 0) {
						tagsToRewrite.put(problem.getLineNumber(), getAttrValues());
						problemsFixed++;
					}
					else if (getNewAttrNames().length > 0) {
						tagsToRewrite.put(problem.getLineNumber(), getAttrNames());
						problemsFixed++;
					}
					else if (getNewTagNames().length > 0) {
						tagsToRewrite.put(problem.getLineNumber(), getTagNames());
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

						if (getNewAttrValues().length > 0) {
							newValue = getNewAttrValues()[i];
						}
						else if (getNewAttrNames().length > 0) {
							newValue = getNewAttrNames()[i];
						}
						else if (getNewTagNames().length > 0) {
							newValue = getNewTagNames()[i];

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

	protected String[] getAttrNames() {
		return new String[0];
	}

	protected String[] getAttrValues() {
		return new String[0];
	}

	protected String[] getNewAttrNames() {
		return new String[0];
	}

	protected String[] getNewAttrValues() {
		return new String[0];
	}

	protected String[] getNewTagNames() {
		return new String[0];
	}

	protected abstract String[] getTagNames();

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

		for (String tagName : getTagNames()) {
			List<SearchResult> jspTagResults = new ArrayList<SearchResult>();

			if ((getTagNames().length > 0) && (getAttrNames().length == 0) && (getAttrValues().length == 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName);
			}
			else if ((getTagNames().length > 0) && (getAttrNames().length > 0) && (getAttrValues().length == 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName, getAttrNames());
			}
			else if ((getTagNames().length > 0) && (getAttrNames().length > 0) && (getAttrValues().length > 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName, getAttrNames(), getAttrValues());
			}

			if (!jspTagResults.isEmpty()) {
				searchResults.addAll(jspTagResults);
			}
		}

		if (getNewAttrNames().length > 0 || getNewAttrValues().length > 0 || getNewTagNames().length > 0) {
			for (SearchResult searchResult : searchResults) {
				searchResult.autoCorrectContext = "jsptag:" + getClass().getName();
			}
		}

		return searchResults;
	}

}
