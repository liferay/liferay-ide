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

import com.liferay.ide.core.ProjectSynchronizer;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.commands.core.code.ImportExistingLiferayWorkspaceCommandKeys;
import com.liferay.ide.upgrade.commands.core.internal.UpgradeCommandsCorePlugin;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.nio.file.Path;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@Component(
	property = "id=" + ImportExistingLiferayWorkspaceCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class ImportExistingLiferayWorkspaceCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		Path path = null;

		try {
			path = _resourceSelection.selectPath(
				"Please select the workspace location.", "The chosen location is not valid liferay workspace location",
				ResourceSelection.WORKSPACE_LOCATION);
		}
		catch (CoreException ce) {
			return UpgradeCommandsCorePlugin.createErrorStatus(ce.getMessage(), ce);
		}

		if (path == null) {
			return Status.CANCEL_STATUS;
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Map<String, String> upgradeContexts = upgradePlan.getUpgradeContexts();

		upgradeContexts.put("targetProjectLocation", path.toString());

		org.eclipse.core.runtime.Path wsLocation = new org.eclipse.core.runtime.Path(path.toString());

		try {
			CoreUtil.openProject(wsLocation.lastSegment(), wsLocation, progressMonitor);

			_projectSynchronizer.synchronizePath(wsLocation, progressMonitor);
		}
		catch (CoreException ce) {
			return UpgradeCommandsCorePlugin.createErrorStatus("Importing Liferay workspace failed.", ce);
		}

		return Status.OK_STATUS;
	}

	@Reference(target = "(type=gradle)")
	private ProjectSynchronizer _projectSynchronizer;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}