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

package com.liferay.ide.gradle.core.upgrade;

import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;
import com.liferay.ide.upgrade.tasks.core.sdk.MigratePluginsSDKTaskKeys;
import com.liferay.ide.upgrade.tasks.core.sdk.UpgradeToLatestPluginsSDKStepKeys;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
	property = {
		"description=" + UpgradeToLatestPluginsSDKStepKeys.DESCRIPTION, "id=" + UpgradeToLatestPluginsSDKStepKeys.ID,
		"imagePath=icons/new.png", "requirement=required", "order=1", "taskId=" + MigratePluginsSDKTaskKeys.ID,
		"title=" + UpgradeToLatestPluginsSDKStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class UpgradeToLatestPlukginsSDKStep extends BaseUpgradeTaskStep {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"select liferay workspace project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		IProject project = projects.get(0);

		try {
			GradleUtil.runGradleTask(project, "upgradePluginsSDK", progressMonitor);

			project.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
		}
		catch (CoreException ce) {
		}

		return Status.OK_STATUS;
	}

	@Reference
	private ResourceSelection _resourceSelection;

}