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

package com.liferay.ide.upgrade.tasks.core.internal.code;

import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.jobs.InitBundleJob;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.LiferayWorkspaceProjectPredicate;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;

import java.util.List;

import org.eclipse.core.resources.IProject;
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
		"id=initialize_server_bundle", "order=1", "stepId=initialize_server_bundle", "title=Initialize Server Bundle"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class InitializeServerBundleStepAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"select liferay workspace project", false, new LiferayWorkspaceProjectPredicate());

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

		return Status.OK_STATUS;
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}