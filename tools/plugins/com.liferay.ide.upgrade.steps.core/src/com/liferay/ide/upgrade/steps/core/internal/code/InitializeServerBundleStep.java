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

package com.liferay.ide.upgrade.steps.core.internal.code;

import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.jobs.InitBundleJob;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.steps.core.ResourceSelection;
import com.liferay.ide.upgrade.steps.core.code.InitializeServerBundleStepKeys;
import com.liferay.ide.upgrade.steps.core.code.SetupDevelopmentEnvironmentStepKeys;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"description=" + InitializeServerBundleStepKeys.DESCRIPTION, "id=" + InitializeServerBundleStepKeys.ID,
		"imagePath=icons/server.gif", "requirement=recommended", "order=3",
		"parentId=" + SetupDevelopmentEnvironmentStepKeys.ID, "title=" + InitializeServerBundleStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class InitializeServerBundleStep extends BaseUpgradeStep {

	@Activate
	public void activate(ComponentContext componentContext) {
		super.activate(_upgradePlanner, componentContext);
	}

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		IProject project = projects.get(0);

		String bundleUrl = WorkspaceConstants.BUNDLE_URL_CE_7_1;

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetVersion = upgradePlan.getTargetVersion();

		if ("7.0".equals(targetVersion)) {
			bundleUrl = WorkspaceConstants.BUNDLE_URL_CE_7_0;
		}
		else if ("7.1".equals(targetVersion)) {
			bundleUrl = WorkspaceConstants.BUNDLE_URL_CE_7_1;
		}

		InitBundleJob job = new InitBundleJob(project, project.getName(), bundleUrl);

		job.schedule();

		try {
			job.join();
		}
		catch (InterruptedException ie) {
			setStatus(UpgradeStepStatus.FAILED);
		}

		setStatus(UpgradeStepStatus.COMPLETED);

		_upgradePlanner.dispatch(new UpgradeStepPerformedEvent(this, Collections.singletonList(project)));

		return Status.OK_STATUS;
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}