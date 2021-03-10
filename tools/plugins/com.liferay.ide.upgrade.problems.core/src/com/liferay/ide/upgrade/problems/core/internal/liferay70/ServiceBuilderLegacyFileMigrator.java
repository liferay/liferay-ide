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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.AutoFileMigratorException;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.internal.LegacyFilesMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=none", "problem.type=legacy", "problem.title=Legacy File should be removed",
		"problem.summary=Remove Legacy File", "version=7.0", "auto.correct=delete-legacy-file"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class ServiceBuilderLegacyFileMigrator extends LegacyFilesMigrator implements AutoFileMigrator {

	@Override
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigratorException {
		int correctCount = 0;

		for (UpgradeProblem problem : upgradeProblems) {
			File resource = problem.getResource();
			
			try {
				FileUtil.delete(resource);
				correctCount++;
			}
			catch (Exception e) {
			}
		}

		return correctCount;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file) {
		IProject project = CoreUtil.getProject(file);

		legacyFiles = new ArrayList<>();
		legacyFolders = new ArrayList<>();

		String relativePath = "/docroot/WEB-INF/src/META-INF";

		IFile portletSpringXML = project.getFile(relativePath + "/portlet-spring.xml");

		if (portletSpringXML.exists()) {
			legacyFiles.add(portletSpringXML);
		}

		IFile shardDataSourceSpringXML = project.getFile(relativePath + "/shard-data-source-spring.xml");

		if (shardDataSourceSpringXML.exists()) {
			legacyFiles.add(shardDataSourceSpringXML);
		}

		// for 6.2 maven project

		IFolder metaInfFolder = project.getFolder("/src/main/resources/META-INF/");

		if (metaInfFolder.exists()) {
			legacyFolders.add(metaInfFolder);
		}

		return findLegacyFiles();
	}

}