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

package com.liferay.ide.upgrade.plan.tasks.core.internal.workspace;

import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.jobs.InitBundleJob;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.plan.tasks.core.WorkspaceTaskStep;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=init_bundle", "requirement=recommended", "order=200", "taskId=migrate_workspace", "title=Init Bundle"
	},
	service = UpgradeTaskStep.class
)
public class InitBundleTaskStep extends WorkspaceTaskStep {

	@Override
	public IStatus execute(IProject project, IProgressMonitor progressMonitor) {
		InitBundleJob job = new InitBundleJob(project, project.getName(), WorkspaceConstants.BUNDLE_URL_CE_7_1);

		job.schedule();

		return Status.OK_STATUS;
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

}