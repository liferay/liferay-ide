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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.PropertiesFileChecker;
import com.liferay.blade.upgrade.liferay70.PropertiesFileChecker.KeyInfo;
import com.liferay.ide.core.util.ListUtil;

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

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=liferay-versions key in Liferay Plugin Packages Properties",
	"problem.summary=In order to deploy this project to 7.0 the liferay-versions property must be set to 7.0.0+",
	"problem.tickets=", "problem.section=", "auto.correct=property", "implName=LiferayVersionsProperties"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class LiferayVersionsProperties extends PropertiesFileMigrator implements AutoMigrator {

	@Override
	public List<Problem> analyze(File file) {
		List<Problem> problems = new ArrayList<>();

		if (file.getName().equals("liferay-plugin-package.properties")) {
			PropertiesFileChecker propertiesFileChecker = new PropertiesFileChecker(file);

			List<KeyInfo> keys = propertiesFileChecker.getInfos("liferay-versions");

			if (ListUtil.isNotEmpty(keys)) {
				String versions = keys.get(0).value;

				if (!versions.matches(".*7\\.[0-9]\\.[0-9].*")) {
					List<SearchResult> results = propertiesFileChecker.findProperties("liferay-versions");

					if (results != null) {
						String sectionHtml = problemSummary;

						for (SearchResult searchResult : results) {
							searchResult.autoCorrectContext = _PREFIX + "liferay-versions";

							problems.add(
								new Problem(
									problemTitle, problemSummary, problemType, problemTickets, file,
									searchResult.startLine, searchResult.startOffset, searchResult.endOffset,
									sectionHtml, searchResult.autoCorrectContext, Problem.STATUS_NOT_RESOLVED,
									Problem.DEFAULT_MARKER_ID, Problem.MARKER_ERROR));
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

			JavaFile javaFile = context.getBundleContext().getService(
				context.getBundleContext().getServiceReference(JavaFile.class));

			IFile propertiesFile = javaFile.getIFile(file);

			int problemsFixed = 0;

			for (Problem problem : problems) {
				if (problem.autoCorrectContext instanceof String) {
					String propertyData = problem.autoCorrectContext;

					if ((propertyData != null) && propertyData.startsWith(_PREFIX)) {
						String propertyValue = propertyData.substring(_PREFIX.length());

						contents = contents.replaceAll(propertyValue + ".*", propertyValue + "=7.0.0+");

						problemsFixed++;
					}
				}
			}

			propertiesFile.setContents(new ByteArrayInputStream(contents.getBytes()), IResource.FORCE, null);

			return problemsFixed;
		}
		catch (CoreException | IOException e) {
		}

		return 0;
	}

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
	}

	private static final String _PREFIX = "property:";

}