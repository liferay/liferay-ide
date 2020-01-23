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

package com.liferay.ide.upgrade.problems.core.internal.liferay71;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.internal.GradleFileMigrator;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=gradle", "problem.title=The artifactId of Spring Extender has changed",
		"problem.tickets=LPS-85710", "problem.summary=The artifactid of spring extender has been changed",
		"problem.section=#spring-extender-artifactid-changed", "version=7.1", "auto.correct=dependency"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class SpringExtenderArtifactIdChanged extends GradleFileMigrator implements AutoFileMigrator {

	@Override
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigrateException {
		int problemsFixed = 0;

		GradleDependencyUpdater gradleDependencyUpdater = getGradleDependencyUpdater(file);

		if (gradleDependencyUpdater == null) {
			return problemsFixed;
		}

		List<Artifact> dependencies = findArtifactsbyArtifactId(gradleDependencyUpdater, _springExtenderArtifactId);

		for (Artifact dependency : dependencies) {
			try {
				Artifact newDependency = new Artifact(
					dependency.getGroupId(), _newSpringExtenderArtifactId, _getArtifactVersion(),
					dependency.getConfiguration(), dependency.getSource());

				gradleDependencyUpdater.updateDependency(false, dependency, newDependency);

				problemsFixed++;
			}
			catch (IOException ioe) {
			}
		}

		return problemsFixed;
	}

	@Override
	protected void addDependenciesToSearch(List<String> atifictIds) {
		artifactIds.add(_springExtenderArtifactId);
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, String artifactId) {
		return findDependencies(file, artifactId);
	}

	private String _getArtifactVersion() {
		IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (liferayWorkspaceProject == null) {
			return "3.0.0";
		}

		return null;
	}

	private String _newSpringExtenderArtifactId = "com.liferay.portal.spring.extender.api";
	private String _springExtenderArtifactId = "com.liferay.portal.spring.extender";

}