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

package com.liferay.ide.upgrade.tasks.core.internal.sdk;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanElementStatus;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionPerformedEvent;
import com.liferay.ide.upgrade.tasks.core.ProjectImporter;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;
import com.liferay.ide.upgrade.tasks.core.sdk.ImportExistingPluginsSDKStepKeys;
import com.liferay.ide.upgrade.tasks.core.sdk.MigratePluginsSDKProjectsTaskKeys;

import java.nio.file.Path;

import java.util.Collections;

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
		"description=" + ImportExistingPluginsSDKStepKeys.DESCRIPTION, "id=" + ImportExistingPluginsSDKStepKeys.ID,
		"imagePath=icons/import.png", "requirement=required", "order=2",
		"taskId=" + MigratePluginsSDKProjectsTaskKeys.ID, "title=" + ImportExistingPluginsSDKStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class ImportExistingPluginsSDKStep extends BaseUpgradeTaskStep {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path rootProjectPath = upgradePlan.getCurrentProjectLocation();

		IStatus status = _projectImporter.canImport(rootProjectPath);

		if (!status.isOK()) {
			setStatus(UpgradePlanElementStatus.FAILED);

			return status;
		}

		status = _projectImporter.importProjects(rootProjectPath, progressMonitor);

		if (!status.isOK()) {
			setStatus(UpgradePlanElementStatus.FAILED);

			return status;
		}

		setStatus(UpgradePlanElementStatus.COMPLETED);

		_upgradePlanner.dispatch(
			new UpgradeTaskStepActionPerformedEvent(this, Collections.singletonList(rootProjectPath)));

		return Status.OK_STATUS;
	}

	@Reference(target = "(type=plugins_sdk)")
	private ProjectImporter _projectImporter;

	@Reference
	private ResourceSelection _projectSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}