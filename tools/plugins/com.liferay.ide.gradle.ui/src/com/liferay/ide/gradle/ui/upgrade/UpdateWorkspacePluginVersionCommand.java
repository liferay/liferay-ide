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
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.UpdateWorkspacePluginVersionKeys;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;

import java.io.File;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = "id=" + UpdateWorkspacePluginVersionKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class UpdateWorkspacePluginVersionCommand implements UpgradeCommand, UpgradePreview {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		File workspaceSettings = _getWorkspaceSettings();

		if (workspaceSettings == null) {
			return Status.CANCEL_STATUS;
		}

		IStatus status = _updateWorkspacePluginVersion(workspaceSettings);

		GradleUtil.refreshProject(_workspaceProject);

		if (status.isOK()) {
			_upgradePlanner.dispatch(
				new UpgradeCommandPerformedEvent(this, Collections.singletonList(workspaceSettings)));
		}

		return status;
	}

	@Override
	public void preview(IProgressMonitor progressMonitor) {
		File workspaceSettings = _getWorkspaceSettings();

		if (workspaceSettings == null) {
			return;
		}

		File tempDir = getTempDir();

		FileUtil.copyFileToDir(workspaceSettings, "settings.gradle-preview", tempDir);

		File tempFile = new File(tempDir, "settings.gradle-preview");

		_updateWorkspacePluginVersion(tempFile);

		UIUtil.async(
			() -> {
				_upgradeCompare.openCompareEditor(workspaceSettings, tempFile);
			});
	}

	private File _getWorkspaceSettings() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		_workspaceProject = projects.get(0);

		IFile workspaceSettings = _workspaceProject.getFile("settings.gradle");

		return FileUtil.getFile(workspaceSettings);
	}

	private IStatus _updateWorkspacePluginVersion(File workspaceSettings) {
		try {
			GradleDependencyUpdater updater = new GradleDependencyUpdater(workspaceSettings);

			List<Artifact> dependencies = updater.getDependencies(true, "classpath");

			dependencies.sort(
				new Comparator<Artifact>() {

					@Override
					public int compare(Artifact artfiactA, Artifact artifactB) {
						return artifactB.getConfiurationEndLineNumber() - artfiactA.getConfiurationEndLineNumber();
					}

				});

			List<String> gradleFileContents = FileUtils.readLines(workspaceSettings);

			Artifact workspacePluginDependency = new Artifact(
				"com.liferay", "com.liferay.gradle.plugins.workspace", _workspace_plugin_version, "classpath", null);

			StringBuilder dependencyBuilder = new StringBuilder(updater.wrapDependency(workspacePluginDependency));

			dependencies.stream(
			).filter(
				artifact -> StringUtil.equals(artifact.getGroupId(), workspacePluginDependency.getGroupId())
			).filter(
				artifact -> StringUtil.equals(artifact.getArtifactId(), workspacePluginDependency.getArtifactId())
			).filter(
				artifact -> CoreUtil.isNotNullOrEmpty(artifact.getVersion())
			).filter(
				artifact -> {
					Version existedVersion = Version.parseVersion(artifact.getVersion());
					Version upgradeVersion = Version.parseVersion(workspacePluginDependency.getVersion());

					return existedVersion.compareTo(upgradeVersion) < 0;
				}
			).forEach(
				configuration -> {
					String configurationContent = gradleFileContents.get(
						configuration.getConfiurationStartLineNumber() - 1);

					int startPos = configurationContent.indexOf(configuration.getConfiguration());

					if (startPos != -1) {
						String prefixString = configurationContent.substring(0, startPos);

						prefixString.chars(
						).filter(
							ch -> ch == '\t'
						).asLongStream(
						).forEach(
							it -> dependencyBuilder.insert(0, "\t")
						);

						gradleFileContents.set(
							configuration.getConfiurationStartLineNumber() - 1, dependencyBuilder.toString());

						for (int i = configuration.getConfiurationEndLineNumber() - 1;
							 i > configuration.getConfiurationStartLineNumber() - 1; i--) {

							gradleFileContents.remove(i);
						}
					}
				}
			);

			FileUtils.writeLines(workspaceSettings, gradleFileContents);

			return Status.OK_STATUS;
		}
		catch (Exception ce) {
			return LiferayGradleUI.createErrorStatus("Unable to configure bundle url", ce);
		}
	}

	private static String _workspace_plugin_version = "1.10.18";

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

	private IProject _workspaceProject;

}