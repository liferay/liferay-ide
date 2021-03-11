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

		if ((_legacyFiles != null) && !_legacyFiles.isEmpty()) {
			for (File legacyFile : _legacyFiles) {
				if (legacyFile.isDirectory()) {
					FileUtil.deleteDir(legacyFile, true);
				}
			}
		}

		return correctCount;
	}

	@Override
	protected List<FileSearchResult> searchFile(File projectDir) {
		_legacyFiles = new ArrayList<>();

		String relativePath = "/docroot/WEB-INF/src/META-INF";

		File portletSpringXML = new File(projectDir.getAbsolutePath() + relativePath + "/portlet-spring.xml");

		if (portletSpringXML.exists()) {
			_legacyFiles.add(portletSpringXML);
		}

		File shardDataSourceSpringXML = new File(
			projectDir.getAbsolutePath() + relativePath + "/shard-data-source-spring.xml");

		if (shardDataSourceSpringXML.exists()) {
			_legacyFiles.add(shardDataSourceSpringXML);
		}

		// for 6.2 maven project

		File metaInfFolder = new File(projectDir.getAbsolutePath() + "/src/main/resources/META-INF/");

		if (metaInfFolder.exists()) {
			_legacyFiles.add(metaInfFolder);
		}

		return findLegacyFiles(_legacyFiles);
	}

	private List<File> _legacyFiles;

}