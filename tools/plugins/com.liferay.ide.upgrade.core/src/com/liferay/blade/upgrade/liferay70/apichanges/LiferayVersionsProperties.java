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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.PropertiesFileChecker;
import com.liferay.blade.upgrade.liferay70.PropertiesFileChecker.KeyInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=properties",
		"problem.title=liferay-versions key in Liferay Plugin Packages Properties",
		"problem.summary=In order to deploy this project to 7.0 the liferay-versions property must be set to 7.0.0+",
		"problem.tickets=",
		"problem.section=",
		"auto.correct=property",
		"implName=LiferayVersionsProperties"
	},
	service = {
		AutoMigrator.class,
		FileMigrator.class
	}
)
public class LiferayVersionsProperties extends PropertiesFileMigrator implements AutoMigrator {

	private static final String PREFIX = "property:";

	@Override
	protected void addPropertiesToSearch(List<String> _properties) {
	}

	@Override
	public List<Problem> analyze(File file) {
		final List<Problem> problems = new ArrayList<>();

		if (file.getName().equals("liferay-plugin-package.properties")) {
			PropertiesFileChecker propertiesFileChecker =
					new PropertiesFileChecker(file);

			List<KeyInfo> keys = propertiesFileChecker.getInfos("liferay-versions");

			if (keys != null && keys.size() > 0) {
				String versions = keys.get(0).value;

				if (!versions.matches(".*7\\.[0-9]\\.[0-9].*")) {
					List<SearchResult> results = propertiesFileChecker.findProperties("liferay-versions");

					if (results != null) {
						String sectionHtml = _problemSummary;

						for (SearchResult searchResult : results) {
							searchResult.autoCorrectContext = PREFIX + "liferay-versions";

							problems.add(new Problem( _problemTitle, _problemSummary,
								_problemType, _problemTickets, file,
								searchResult.startLine, searchResult.startOffset,
								searchResult.endOffset, sectionHtml, searchResult.autoCorrectContext,
								Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, Problem.MARKER_ERROR));
						}
					}
				}
			}
		}

		return problems;
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		try {
			String contents = new String(Files.readAllBytes(file.toPath()));

			final JavaFile javaFile = _context.getBundleContext()
					.getService(_context.getBundleContext().getServiceReference(JavaFile.class));
			final IFile propertiesFile = javaFile.getIFile(file);

			int problemsFixed = 0;
			for (Problem problem : problems) {
				if (problem.autoCorrectContext instanceof String) {
					final String propertyData = problem.autoCorrectContext;

					if (propertyData != null && propertyData.startsWith(PREFIX)) {
						final String propertyValue = propertyData.substring(PREFIX.length());

						contents = contents.replaceAll(propertyValue+".*", propertyValue + "=7.0.0+");
						problemsFixed++;
					}
				}
			}

			propertiesFile.setContents(new ByteArrayInputStream(contents.getBytes()), IResource.FORCE, null);

			return problemsFixed;
		} catch (CoreException | IOException e) {
		}

		return 0;
	}

}
