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
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.upgrade.commands.core.ResourceSelection;
import com.liferay.ide.upgrade.commands.core.sdk.CreateLegacyPluginsSDKCommandKeys;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.util.Collections;
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
 * @author Gregory Amerson
 */
@Component(
	property = "id=" + CreateLegacyPluginsSDKCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class CreateLegacyPluginsSDKCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		IProject project = projects.get(0);

		try {
			GradleUtil.runGradleTask(project, "upgradePluginsSDK", progressMonitor);

			project.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);

			_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(project)));

			return Status.OK_STATUS;
		}
		catch (CoreException ce) {
			IStatus error = LiferayGradleCore.createErrorStatus("Unable to run task upgradePluginsSDK", ce);

			LiferayGradleCore.log(error);

			return error;
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}