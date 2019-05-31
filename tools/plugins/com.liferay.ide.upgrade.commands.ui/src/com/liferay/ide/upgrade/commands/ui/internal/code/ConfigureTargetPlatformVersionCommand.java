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

package com.liferay.ide.upgrade.commands.ui.internal.code;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.ConfigureTargetPlatformVersionCommandKeys;
import com.liferay.ide.upgrade.commands.ui.internal.UpgradeCommandsUIPlugin;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = "id=" + ConfigureTargetPlatformVersionCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class ConfigureTargetPlatformVersionCommand implements UpgradeCommand, UpgradePreview {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		File gradleProperties = _getGradlePropertiesFile();

		if (gradleProperties == null) {
			return Status.CANCEL_STATUS;
		}

		IStatus status = _updateTargetPlatformValue(gradleProperties);

		if (status.isOK()) {
			_upgradePlanner.dispatch(
				new UpgradeCommandPerformedEvent(this, Collections.singletonList(gradleProperties)));
		}

		return status;
	}

	@Override
	public void preview(IProgressMonitor progressMonitor) {
		File gradeProperties = _getGradlePropertiesFile();

		if (gradeProperties == null) {
			return;
		}

		File tempDir = getTempDir();

		FileUtil.copyFileToDir(gradeProperties, "gradle.properties-preview", tempDir);

		File tempFile = new File(tempDir, "gradle.properties-preview");

		_updateTargetPlatformValue(tempFile);

		UIUtil.async(
			() -> {
				_upgradeCompare.openCompareEditor(gradeProperties, tempFile);
			});
	}

	private File _getGradlePropertiesFile() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		IProject project = projects.get(0);

		IFile gradeProperties = project.getFile("gradle.properties");

		return FileUtil.getFile(gradeProperties);
	}

	private IStatus _updateTargetPlatformValue(File gradeProperties) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.1")[0];

		String targetVersion = upgradePlan.getTargetVersion();

		if ("7.0".equals(targetVersion)) {
			targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.0")[0];
		}
		else if ("7.1".equals(targetVersion)) {
			targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.1")[0];
		}
		else if ("7.2".equals(targetVersion)) {
			targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.2")[0];
		}

		try {
			PropertiesConfiguration config = new PropertiesConfiguration(gradeProperties);

			config.setProperty(WorkspaceConstants.TARGET_PLATFORM_VERSION_PROPERTY, targetPlatformVersion);

			config.save();

			return Status.OK_STATUS;
		}
		catch (ConfigurationException ce) {
			return UpgradeCommandsUIPlugin.createErrorStatus("Unable to configure target platform", ce);
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

}