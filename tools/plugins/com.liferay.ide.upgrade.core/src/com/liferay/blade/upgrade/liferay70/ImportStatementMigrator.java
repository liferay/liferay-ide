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
import com.liferay.blade.api.CUCache;
import com.liferay.blade.api.JavaFile;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("rawtypes")
public abstract class ImportStatementMigrator extends AbstractFileMigrator<JavaFile> implements AutoMigrator {

	private static final String PREFIX = "import:";
	private final Map<String, String> _importFixes;

	public ImportStatementMigrator(Map<String, String> importFixes) {
		super(JavaFile.class);

		_importFixes = importFixes;
	}

	private void clearCache(File file) {
		try {
			Collection<ServiceReference<CUCache>> src = _context.getServiceReferences(CUCache.class, null);

			for (ServiceReference<CUCache> sr : src) {
				CUCache cache = _context.getService(sr);
				cache.unget(file);
			}
		}
		catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int problemsFixed = 0;

		final List<String> importsToRewrite = new ArrayList<>();

		for (Problem problem : problems) {
			boolean problemFound = false;

			if (problem.autoCorrectContext instanceof String) {
				final String importData = problem.autoCorrectContext;

				if (importData != null && importData.startsWith(PREFIX)) {
					final String importValue = importData.substring(PREFIX.length());

					if (_importFixes.containsKey(importValue)) {
						importsToRewrite.add(problem.getLineNumber() + "," + importValue);

						problemFound = true;
					}
				}
			}

			if (problemFound) {
				problemsFixed++;
			}
		}

		if (importsToRewrite.size() > 0) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				String[] lines = readLines(inputStream);
				inputStream.close();

				String[] editedLines = new String[lines.length];
				System.arraycopy(lines, 0, editedLines, 0, lines.length);

				for (String importData : importsToRewrite) {
					String[] importMap = importData.split(",");

					int lineNumber = Integer.parseInt(importMap[0]);
					String importName = importMap[1];

					editedLines[lineNumber - 1] =
						editedLines[lineNumber - 1].replaceAll(importName, _importFixes.get(importName));
				}

				StringBuilder sb = new StringBuilder();
				for (String editedLine : editedLines) {
					sb.append(editedLine);
					sb.append(System.getProperty("line.separator"));
				}

				FileWriter writer = new FileWriter(file);
				writer.write(sb.toString());
				writer.close();

				clearCache(file);

				return problemsFixed;
			} catch (IOException e) {
				throw new AutoMigrateException("Unable to auto-correct", e);
			}
		}

		return 0;
	}

	protected IFile getJavaFile(File file) {
		final JavaFile javaFileService = _context.getService(_context.getServiceReference(JavaFile.class));

		return javaFileService.getIFile(file);
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
		}

		return lines.toArray(new String[lines.size()]);
	}

	@Override
	public List<SearchResult> searchFile(File file, JavaFile javaFile) {
		final List<SearchResult> searchResults = new ArrayList<>();

		for (String importName : _importFixes.keySet()) {
			final SearchResult importResult = javaFile.findImport(importName);

			if (importResult != null) {
				importResult.autoCorrectContext = PREFIX + importName;

				searchResults.add(importResult);
			}
		}

		return searchResults;
	}

	public static String getPrefix() {
		return PREFIX;
	}

}
