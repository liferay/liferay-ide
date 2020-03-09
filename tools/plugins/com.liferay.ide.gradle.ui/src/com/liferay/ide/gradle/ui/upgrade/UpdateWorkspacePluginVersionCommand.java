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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.UpdateWorkspacePluginVersionKeys;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@Component(
	property = "id=" + UpdateWorkspacePluginVersionKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class UpdateWorkspacePluginVersionCommand implements UpgradeCommand, UpgradePreview {

	public UpdateWorkspacePluginVersionCommand() {
		try {
			IPath bladeCLIPath = BladeCLI.getBladeCLIPath();

			File bladeJarFile = bladeCLIPath.toFile();

			if (FileUtil.exists(bladeJarFile)) {
				_workspacePluginLatestVersion = _getWorkspacePluginVersion(bladeJarFile);
			}
		}
		catch (Exception e) {
			LiferayGradleUI.logError("Failed to find latest workspace plugin latest version.", e);
		}
	}

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

	private String _getWorkspacePluginVersion(File file) throws Exception {
		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();

				String entryName = zipEntry.getName();

				if (entryName.startsWith(_WORKSPACE_TEMPLATE_NAME) && entryName.endsWith(".jar")) {
					Path jarFile = Files.createTempFile(_WORKSPACE_TEMPLATE_NAME, ".jar");

					FileUtil.writeFile(jarFile.toFile(), zipFile.getInputStream(zipEntry));

					try (ZipFile workspaceJarZipFile = new ZipFile(jarFile.toFile())) {
						Enumeration<? extends ZipEntry> workspaceEntries = workspaceJarZipFile.entries();

						while (workspaceEntries.hasMoreElements()) {
							ZipEntry workspaceZipEntry = workspaceEntries.nextElement();

							String workspaceEntryName = workspaceZipEntry.getName();

							if (workspaceEntryName.endsWith("settings.gradle")) {
								Path settingsFile = Files.createTempFile("settings", ".gradle");

								String settingsGradle = FileUtil.readContents(
									workspaceJarZipFile.getInputStream(workspaceZipEntry));

								Files.delete(settingsFile);

								GradleBuildScript gradleBuildScript = new GradleBuildScript(settingsGradle);

								List<GradleDependency> classpathDependencies =
									gradleBuildScript.getBuildScriptDependencies();

								return classpathDependencies.stream(
								).filter(
									artifact -> _WORKSPACE_PLUGIN_GROUP_ID.equals(artifact.getGroup())
								).filter(
									artifact -> _WORKSPACE_PLUGIN_ARTIFACT_ID.equals(artifact.getName())
								).map(
									artifact -> artifact.getVersion()
								).findFirst(
								).orElseGet(
									() -> _workspacePluginLatestVersion
								);
							}
						}
					}
					finally {
						Files.delete(jarFile);
					}
				}
			}
		}

		return _workspacePluginLatestVersion;
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
			GradleBuildScript gradleBuildScript = new GradleBuildScript(workspaceSettings);

			List<GradleDependency> dependencies = gradleBuildScript.getBuildScriptDependencies();

			dependencies.stream(
			).filter(
				artifact -> _WORKSPACE_PLUGIN_GROUP_ID.equals(artifact.getGroup())
			).filter(
				artifact -> _WORKSPACE_PLUGIN_ARTIFACT_ID.equals(artifact.getName())
			).filter(
				artifact -> CoreUtil.isNotNullOrEmpty(artifact.getVersion())
			).filter(
				artifact -> {
					Version existedVersion = Version.parseVersion(artifact.getVersion());
					Version upgradeVersion = Version.parseVersion(_workspacePluginLatestVersion);

					return existedVersion.compareTo(upgradeVersion) < 0;
				}
			).findFirst(
			).ifPresent(
				artifact -> {
					artifact.setVersion(_workspacePluginLatestVersion);

					try {
						gradleBuildScript.updateDependencies(Collections.singletonList(artifact));
					}
					catch (IOException ioe) {
						LiferayGradleUI.logError(ioe);
					}
				}
			);

			return Status.OK_STATUS;
		}
		catch (IOException ioe) {
			return LiferayGradleUI.createErrorStatus("Unable to configure workspace plugin version.", ioe);
		}
	}

	private static final String _WORKSPACE_PLUGIN_ARTIFACT_ID = "com.liferay.gradle.plugins.workspace";

	private static final String _WORKSPACE_PLUGIN_GROUP_ID = "com.liferay";

	private static final String _WORKSPACE_TEMPLATE_NAME = "com.liferay.project.templates.workspace";

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

	private String _workspacePluginLatestVersion = "2.0.3";
	private IProject _workspaceProject;

}