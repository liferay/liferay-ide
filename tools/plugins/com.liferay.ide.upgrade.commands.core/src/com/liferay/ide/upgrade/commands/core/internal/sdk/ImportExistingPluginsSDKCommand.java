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

package com.liferay.ide.upgrade.commands.core.internal.sdk;

import com.liferay.ide.upgrade.commands.core.ProjectImporter;
import com.liferay.ide.upgrade.commands.core.sdk.ImportExistingPluginsSDKCommandKeys;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

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
	property = "id=" + ImportExistingPluginsSDKCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class ImportExistingPluginsSDKCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path rootProjectPath = upgradePlan.getCurrentProjectLocation();

		IStatus status = _projectImporter.canImport(rootProjectPath);

		if (!status.isOK()) {
			return status;
		}

		status = _projectImporter.importProjects(rootProjectPath, progressMonitor);

		if (!status.isOK()) {
			return status;
		}

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(rootProjectPath)));

		return Status.OK_STATUS;
	}

	@Reference(target = "(type=plugins_sdk)")
	private ProjectImporter _projectImporter;

	@Reference
	private ResourceSelection _projectSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}