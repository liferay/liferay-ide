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

package com.liferay.ide.upgrade.problems.core.internal.commands;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.util.VersionUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblemSupport;
import com.liferay.ide.upgrade.problems.core.commands.SwitchToUseReleaseAPIDependencyCommandKeys;
import com.liferay.ide.upgrade.problems.core.internal.UpgradeProblemsCorePlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Seiphon Wang
 */
@Component(
	property = "id=" + SwitchToUseReleaseAPIDependencyCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class SwitchToUseReleaseAPIDependencyCommand implements UpgradeCommand, UpgradeProblemSupport {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select a Liferay Project", true, ResourceSelection.LIFERAY_PROJECTS);

		Collection<File> buildGradleFiles = GradleUtil.getBuildGradleFiles(projects);

		if (buildGradleFiles == null) {
			return Status.CANCEL_STATUS;
		}

		Stream<File> buildGradleStrem = buildGradleFiles.stream();

		List<String> allArtifactIds = _getAllDependenciesArtifactIds();

		buildGradleStrem.forEach(buildGradle -> _replaceDependencisWithReleaseAPI(buildGradle, allArtifactIds));

		GradleUtil.refreshProject(LiferayWorkspaceUtil.getWorkspaceProject());

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(buildGradleFiles)));

		return Status.OK_STATUS;
	}

	private List<String> _getAllDependenciesArtifactIds() {
		IWorkspaceProject gradleWorkspaceProject = LiferayWorkspaceUtil.getGradleWorkspaceProject();

		if (Objects.isNull(gradleWorkspaceProject)) {
			return Collections.emptyList();
		}

		List<String> allArtifactIds = new ArrayList<>();

		String productKey = gradleWorkspaceProject.getProperty(WorkspaceConstants.WORKSPACE_PRODUCT_PROPERTY, null);

		String simplifiedVersion = VersionUtil.simplifyTargetPlatformVersion(
			gradleWorkspaceProject.getTargetPlatformVersion());

		String[] versionParts = simplifiedVersion.split("\\.");

		if (productKey.startsWith("dxp")) {
			simplifiedVersion = versionParts[0] + "." + versionParts[1] + "." + versionParts[2] + ".x";
		}
		else if (productKey.startsWith("portal")) {
			simplifiedVersion = versionParts[0] + "." + versionParts[1] + ".x";
		}

		Class<?> clazz = SwitchToUseReleaseAPIDependencyCommand.class;

		try (InputStream inputStream = clazz.getResourceAsStream("/release-api/" + simplifiedVersion + "-versions.txt");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

			String dependency = null;

			while ((dependency = bufferedReader.readLine()) != null) {
				String[] segments = dependency.split(":");

				allArtifactIds.add(segments[1]);
			}
		}
		catch (Exception e) {
		}

		return allArtifactIds;
	}

	private GradleBuildScript _getGradleBuildScript(File file) {
		GradleBuildScript gradleBuildScript = null;

		try {
			gradleBuildScript = new GradleBuildScript(file);
		}
		catch (IOException ioe) {
			return null;
		}

		return gradleBuildScript;
	}

	private void _replaceDependencisWithReleaseAPI(File buildGradleFile, List<String> allArtifactIds) {
		GradleBuildScript gradleBuildScript = _getGradleBuildScript(buildGradleFile);

		if ((gradleBuildScript == null) || allArtifactIds.isEmpty()) {
			return;
		}

		List<GradleDependency> gradleDependencies = gradleBuildScript.getDependencies();

		Stream<GradleDependency> dependencyStream = gradleDependencies.stream();

		List<GradleDependency> dependencies = dependencyStream.filter(
			dep -> allArtifactIds.contains(dep.getName())
		).collect(
			Collectors.toList()
		);

		try {
			gradleBuildScript.deleteDependency(dependencies);

			FileUtils.writeLines(buildGradleFile, gradleBuildScript.getFileContents());

			GradleUtil.refreshProject(LiferayWorkspaceUtil.getWorkspaceProject());
		}
		catch (Exception e) {
			UpgradeProblemsCorePlugin.logError(
				"Replace dependencies failed when process " + buildGradleFile.getPath(), e);
		}

		String configuration = "compileOnly";
		String groupId = "com.liferay.portal";

		IWorkspaceProject gradleWorkspace = LiferayWorkspaceUtil.getGradleWorkspaceProject();

		if (gradleWorkspace != null) {
			String productVersion = gradleWorkspace.getProperty(WorkspaceConstants.WORKSPACE_PRODUCT_PROPERTY, null);

			if (Objects.isNull(productVersion)) {
				return;
			}

			gradleBuildScript = _getGradleBuildScript(buildGradleFile);

			gradleDependencies = gradleBuildScript.getDependencies();

			dependencyStream = gradleDependencies.stream();

			dependencies = dependencyStream.filter(
				dep -> StringUtil.equals(groupId, dep.getGroup()) && StringUtil.equals(_portalArtifactId, dep.getName())
			).collect(
				Collectors.toList()
			);

			GradleDependency newDependency = null;

			try {
				if (productVersion.startsWith("dxp")) {
					if (!dependencies.isEmpty()) {
						GradleDependency oldDependency = dependencies.get(0);

						newDependency = new GradleDependency(
							configuration, groupId, _dxpArtifactId, null, oldDependency.getLineNumber(),
							oldDependency.getLastLineNumber());

						gradleBuildScript.updateDependency(oldDependency, newDependency);
					}
					else {
						newDependency = new GradleDependency(configuration, groupId, _dxpArtifactId, null, 0, 0);

						gradleBuildScript.insertDependency(newDependency);
					}
				}
				else {
					if (dependencies.isEmpty()) {
						newDependency = new GradleDependency(configuration, groupId, _portalArtifactId, null, 0, 0);

						gradleBuildScript.insertDependency(newDependency);
					}
				}

				FileUtils.writeLines(buildGradleFile, gradleBuildScript.getFileContents());
			}
			catch (IOException e) {
				UpgradeProblemsCorePlugin.logError(
					"Replace dependencies failed when process " + buildGradleFile.getPath(), e);
			}
		}
	}

	private String _dxpArtifactId = "release.dxp.api";
	private String _portalArtifactId = "release.portal.api";

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}