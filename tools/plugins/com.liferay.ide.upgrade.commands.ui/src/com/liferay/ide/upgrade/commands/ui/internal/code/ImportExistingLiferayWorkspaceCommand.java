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

package com.liferay.ide.upgrade.commands.ui.internal.code;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.ui.workspace.ImportLiferayWorkspaceWizard;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.UpgradeCommandsCorePlugin;
import com.liferay.ide.upgrade.commands.core.code.ImportExistingLiferayWorkspaceCommandKeys;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Ethan Sun
 * @author Simon Jiang
 */
@Component(
	property = "id=" + ImportExistingLiferayWorkspaceCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class ImportExistingLiferayWorkspaceCommand implements UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		final AtomicInteger returnCode = new AtomicInteger();

		UIUtil.sync(
			() -> {
				ImportLiferayWorkspaceWizard importLiferayWorkspaceWizard = new ImportLiferayWorkspaceWizard(false) {

					@Override
					protected void openLiferayPerspective(IProject newProject) {
					}

				};

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				WizardDialog wizardDialog = new WizardDialog(workbenchWindow.getShell(), importLiferayWorkspaceWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.CANCEL) {
			IProject[] projects = CoreUtil.getAllProjects();

			if (projects.length == 0) {
				return UpgradeCommandsCorePlugin.createErrorStatus("A Liferay Workspace Needs To Be Specified");
			}

			return Status.OK_STATUS;
		}

		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject) || !workspaceProject.exists()) {
			return UpgradeCommandsCorePlugin.createErrorStatus("A Liferay Workspace Needs To Be Specified");
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Map<String, String> upgradeContext = upgradePlan.getUpgradeContext();

		IPath workspaceProjectLocation = workspaceProject.getLocation();

		upgradeContext.put("targetProjectLocation", workspaceProjectLocation.toOSString());

		return Status.OK_STATUS;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}