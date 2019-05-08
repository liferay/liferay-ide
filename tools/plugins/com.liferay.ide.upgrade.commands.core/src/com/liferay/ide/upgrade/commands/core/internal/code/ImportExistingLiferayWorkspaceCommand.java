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
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;
import com.liferay.ide.upgrade.commands.core.code.ImportExistingLiferayWorkspaceCommandKeys;
import com.liferay.ide.upgrade.commands.core.internal.UpgradeCommandsCorePlugin;
import com.liferay.ide.upgrade.plan.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.nio.file.Path;

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
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (FileUtil.exists(targetProjectLocation)) {
			boolean result = _messagePrompt.promptQuestion(
				"Target Project Location Exists",
				"The path " + targetProjectLocation +
					" already is used as target project location, do you want to override?");

			if (!result) {
				return Status.CANCEL_STATUS;
			}
		}

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

		upgradePlan.setTargetProjectLocation(path);

		return _provider.importProject(new org.eclipse.core.runtime.Path(path.toString()), progressMonitor);
	}

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference(target = "(type=gradle_workspace)")
	private NewLiferayWorkspaceProjectProvider<?> _provider;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}