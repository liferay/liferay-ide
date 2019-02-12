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

package com.liferay.ide.upgrade.tasks.ui.internal;

import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.ui.workspace.NewLiferayWorkspaceWizard;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;

import java.nio.file.Paths;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.swt.widgets.Shell;
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
	property = {
		"id=new_liferay_workspace", "order=1", "stepId=create_liferay_workspace", "title=New Liferay Workspace"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class NewLiferayWorkspaceAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		NewLiferayWorkspaceOp newLiferayWorkspaceOp = NewLiferayWorkspaceOp.TYPE.instantiate();

		final AtomicInteger returnCode = new AtomicInteger();

		UIUtil.sync(
			() -> {
				NewLiferayWorkspaceWizard newLiferayWorkspaceWizard = new NewLiferayWorkspaceWizard(
					newLiferayWorkspaceOp);

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				Shell shell = workbenchWindow.getShell();

				WizardDialog wizardDialog = new WizardDialog(shell, newLiferayWorkspaceWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.OK) {
			Value<Path> location = newLiferayWorkspaceOp.getLocation();

			UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

			Path content = location.content();

			upgradePlan.setTargetProjectLocation(Paths.get(content.toOSString()));
		}

		return Status.OK_STATUS;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}