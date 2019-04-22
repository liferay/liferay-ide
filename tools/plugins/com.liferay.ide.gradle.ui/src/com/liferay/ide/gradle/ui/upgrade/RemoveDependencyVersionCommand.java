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

package com.liferay.ide.gradle.ui.upgrade;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.upgrade.commands.core.code.RemoveDependencyVersionKeys;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.io.File;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = "id=" + RemoveDependencyVersionKeys.ID, scope = ServiceScope.PROTOTYPE, service = UpgradeCommand.class
)
public class RemoveDependencyVersionCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IFile> buildGradleFiles = _geLiferayProjectBuildGradleFiles();

		if (buildGradleFiles == null) {
			return Status.CANCEL_STATUS;
		}

		IStatus status = _removeDependencyVersion(buildGradleFiles);

		GradleUtil.refreshProject(LiferayWorkspaceUtil.getWorkspaceProject());

		if (status.isOK()) {
			_upgradePlanner.dispatch(
				new UpgradeCommandPerformedEvent(this, Collections.singletonList(buildGradleFiles)));
		}

		return status;
	}

	private List<IFile> _geLiferayProjectBuildGradleFiles() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Project", false, ResourceSelection.JAVA_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		return projects.stream(
		).map(
			project -> project.getFile("build.gradle")
		).collect(
			Collectors.toList()
		);
	}

	private IStatus _removeDependencyVersion(List<IFile> buildGradleFiles) {
		try {
			IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getGradleWorkspaceProject();

			List<Artifact> targetPlatformArtifacts = liferayWorkspaceProject.getTargetPlatformArtifacts();

			buildGradleFiles.stream(
			).forEach(
				buildGradle -> {
					try {
						IPath buildGradlePagth = buildGradle.getLocation();

						File buildGradeFile = buildGradlePagth.toFile();

						GradleDependencyUpdater updater = new GradleDependencyUpdater(buildGradeFile);

						List<Artifact> dependencies = updater.getDependencies("*");

						dependencies.sort(
							new Comparator<Artifact>() {

								@Override
								public int compare(Artifact artfiactA, Artifact artifactB) {
									return artifactB.getConfiurationEndLineNumber() -
										artfiactA.getConfiurationEndLineNumber();
								}

							});

						List<String> gradleFileContents = FileUtils.readLines(buildGradeFile);

						dependencies.stream(
						).forEach(
							artifact -> {
								artifact.setVersion(null);

								if (targetPlatformArtifacts.contains(artifact)) {
									_updateCommonDependency(updater, gradleFileContents, artifact);
								}
							}
						);

						FileUtils.writeLines(buildGradeFile, gradleFileContents);
					}
					catch (Exception e) {
					}
				}
			);

			return Status.OK_STATUS;
		}
		catch (Exception ce) {
			return LiferayGradleUI.createErrorStatus("Unable to configure bundle url", ce);
		}
	}

	private void _updateCommonDependency(
		GradleDependencyUpdater updater, List<String> gradleFileContents, Artifact artifact) {

		try {
			StringBuilder dependencyBuilder = new StringBuilder(updater.wrapDependency(artifact));

			String configurationContent = gradleFileContents.get(artifact.getConfiurationStartLineNumber() - 1);

			int startPos = configurationContent.indexOf(artifact.getConfiguration());

			if (startPos == -1) {
				return;
			}

			String prefixString = configurationContent.substring(0, startPos);

			prefixString.chars(
			).filter(
				ch -> ch == '\t'
			).asLongStream(
			).forEach(
				it -> dependencyBuilder.insert(0, "\t")
			);

			gradleFileContents.set(artifact.getConfiurationStartLineNumber() - 1, dependencyBuilder.toString());

			for (int i = artifact.getConfiurationEndLineNumber() - 1; i > artifact.getConfiurationStartLineNumber() - 1;
				 i--) {

				gradleFileContents.remove(i);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}