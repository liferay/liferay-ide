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

package com.liferay.ide.upgrade.commands.core.internal.code;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.upgrade.commands.core.code.ConfigureTargetPlatformVersionCommandKeys;
import com.liferay.ide.upgrade.commands.core.internal.UpgradeCommandsCorePlugin;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

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
	service = UpgradeCommand.class
)
public class ConfigureTargetPlatformVersionCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		IProject project = projects.get(0);

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.1")[0];

		String targetVersion = upgradePlan.getTargetVersion();

		if ("7.0".equals(targetVersion)) {
			targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.0")[0];
		}
		else if ("7.1".equals(targetVersion)) {
			targetPlatformVersion = WorkspaceConstants.liferayTargetPlatformVersions.get("7.1")[0];
		}

		IFile gradeProperties = project.getFile("gradle.properties");

		if (FileUtil.exists(gradeProperties)) {
			try {
				PropertiesConfiguration config = new PropertiesConfiguration(FileUtil.getFile(gradeProperties));

				config.setProperty(WorkspaceConstants.TARGET_PLATFORM_VERSION_PROPERTY, targetPlatformVersion);

				config.save();
			}
			catch (ConfigurationException ce) {
				return UpgradeCommandsCorePlugin.createErrorStatus("Unable to configure target platform", ce);
			}
		}

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(project)));

		return Status.OK_STATUS;
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}