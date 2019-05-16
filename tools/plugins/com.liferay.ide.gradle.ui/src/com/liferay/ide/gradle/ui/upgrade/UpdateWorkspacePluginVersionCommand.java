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
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.UpdateWorkspacePluginVersionKeys;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
		catch (BladeCLIException bclie) {
			LiferayGradleUI.logError("Failed to find blade jar file", bclie);
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

	private String _getWorkspacePluginVersion(File file) {
		try (ZipInputStream bladeJarInputStream = new ZipInputStream(new FileInputStream(file))) {
			ZipEntry zipEntry = null;

			while ((zipEntry = bladeJarInputStream.getNextEntry()) != null) {
				String entryName = zipEntry.getName();

				if (entryName.startsWith(_workspace_template_name) && entryName.endsWith(".jar")) {
					try (ZipInputStream workspaceJarInputStream = new ZipInputStream(
							ZipUtil.getEmbedStream(bladeJarInputStream, zipEntry))) {

						ZipEntry workspaceEntry = null;

						while ((workspaceEntry = workspaceJarInputStream.getNextEntry()) != null) {
							String workspaceEntryName = workspaceEntry.getName();

							if (workspaceEntryName.endsWith("settings.gradle")) {
								try (InputStream settingsGradleInputStream = ZipUtil.getEmbedStream(
										workspaceJarInputStream, workspaceEntry)) {

									String settingsGradle = FileUtil.readContents(settingsGradleInputStream);

									GradleDependencyUpdater gradleDependencyUpdater = new GradleDependencyUpdater(
										settingsGradle);

									List<Artifact> classpathDependencies = gradleDependencyUpdater.getDependencies(
										true, "classpath");

									return classpathDependencies.stream(
									).filter(
										artifact -> _workspace_plugin_groupId.equals(artifact.getGroupId())
									).filter(
										artifact -> _workspace_plugin_artficatId.equals(artifact.getArtifactId())
									).map(
										artifact -> artifact.getVersion()
									).findFirst(
									).orElseGet(
										() -> _workspacePluginLatestVersion
									);
								}
							}
						}
					}
				}
			}
		}
		catch (IOException ioe) {
			LiferayGradleUI.logError("Failed to get workspace plugin version", ioe);
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
			GradleDependencyUpdater gradleDependencyUpdater = new GradleDependencyUpdater(workspaceSettings);

			List<Artifact> dependencies = gradleDependencyUpdater.getDependencies(true, "classpath");

			dependencies.stream(
			).filter(
				artifact -> _workspace_plugin_groupId.equals(artifact.getGroupId())
			).filter(
				artifact -> _workspace_plugin_artficatId.equals(artifact.getArtifactId())
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
						gradleDependencyUpdater.updateDependencyVersions(true, Collections.singletonList(artifact));
					}
					catch (IOException ioe) {
						LiferayGradleUI.logError(ioe);
					}
				}
			);

			return Status.OK_STATUS;
		}
		catch (IOException ioe) {
			return LiferayGradleUI.createErrorStatus("Unable to configure bundle url", ioe);
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

	private String _workspace_plugin_artficatId = "com.liferay.gradle.plugins.workspace";
	private String _workspace_plugin_groupId = "com.liferay";
	private String _workspace_template_name = "com.liferay.project.templates.workspace";
	private String _workspacePluginLatestVersion = "2.0.1";
	private IProject _workspaceProject;

}