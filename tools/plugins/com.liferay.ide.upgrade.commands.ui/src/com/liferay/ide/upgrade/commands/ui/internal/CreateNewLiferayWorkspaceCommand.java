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

package com.liferay.ide.upgrade.commands.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.nio.file.Paths;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = "id=" + CreateNewLiferayWorkspaceCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class CreateNewLiferayWorkspaceCommand implements SapphireContentAccessor, UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Map<String, String> upgradeContext = upgradePlan.getUpgradeContext();

		String targetProjectLocation = upgradeContext.get("targetProjectLocation");

		if (targetProjectLocation != null) {
			boolean result = _messagePrompt.promptQuestion(
				"Target Project Location Exists",
				"The path " + targetProjectLocation +
					" already is used as target project location, do you want to override?");

			if (!result) {
				return Status.CANCEL_STATUS;
			}
		}

		NewLiferayWorkspaceOp newLiferayWorkspaceOp = NewLiferayWorkspaceOp.TYPE.instantiate();

		switch (upgradePlan.getTargetVersion()) {
			case "7.0":
				newLiferayWorkspaceOp.setProductVersion("portal-7.0-ga7");

				break;
			case "7.1":
				newLiferayWorkspaceOp.setProductVersion("portal-7.1-ga4");

				break;
			case "7.2":
				newLiferayWorkspaceOp.setProductVersion("portal-7.2-ga2");

				break;
			case "7.3":
				newLiferayWorkspaceOp.setProductVersion("portal-7.3-ga7");

				break;
			case "7.4":
				newLiferayWorkspaceOp.setProductVersion("portal-7.4-ga2");

				break;
		}

		newLiferayWorkspaceOp.setLiferayVersion(upgradePlan.getTargetVersion());

		final AtomicInteger returnCode = new AtomicInteger();

		UIUtil.sync(
			() -> {
				NewBasicLiferayWorkspaceWizard newLiferayWorkspaceWizard = new NewBasicLiferayWorkspaceWizard(
					newLiferayWorkspaceOp);

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				WizardDialog wizardDialog = new WizardDialog(workbenchWindow.getShell(), newLiferayWorkspaceWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.OK) {
			Path parentPath = get(newLiferayWorkspaceOp.getLocation());

			java.nio.file.Path path = Paths.get(parentPath.toOSString());

			String workspaceName = get(newLiferayWorkspaceOp.getWorkspaceName());

			path = path.resolve(workspaceName);

			upgradeContext.put("targetProjectLocation", path.toString());

			_upgradePlanner.dispatch(
				new UpgradeCommandPerformedEvent(this, Collections.singletonList(CoreUtil.getProject(workspaceName))));

			return Status.OK_STATUS;
		}

		return UpgradeCommandsUIPlugin.createErrorStatus("New Liferay Workspace was not created.");
	}

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private UpgradePlanner _upgradePlanner;

}