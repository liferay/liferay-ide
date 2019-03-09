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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanElementStatus;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionPerformedEvent;
import com.liferay.ide.upgrade.tasks.core.code.SetupLiferayWorkspaceStepKeys;

import java.nio.file.Paths;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
		"id=new_liferay_workspace", "order=1", "requirement=required", "stepId=" + SetupLiferayWorkspaceStepKeys.ID,
		"title=New Liferay Workspace"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class NewLiferayWorkspaceAction extends BaseUpgradeTaskStepAction implements SapphireContentAccessor {

	@Override
	public boolean appliesTo(UpgradePlan upgradePlan) {
		java.nio.file.Path path = upgradePlan.getCurrentProjectLocation();

		if (LiferayWorkspaceUtil.isValidWorkspaceLocation(path.toString())) {
			return false;
		}

		return true;
	}

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		NewLiferayWorkspaceOp newLiferayWorkspaceOp = NewLiferayWorkspaceOp.TYPE.instantiate();

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		newLiferayWorkspaceOp.setLiferayVersion(upgradePlan.getTargetVersion());

		final AtomicInteger returnCode = new AtomicInteger();

		UIUtil.sync(
			() -> {
				NewBasicLiferayWorkspaceWizard newLiferayWorkspaceWizard = new NewBasicLiferayWorkspaceWizard(
					newLiferayWorkspaceOp);

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				Shell shell = workbenchWindow.getShell();

				WizardDialog wizardDialog = new WizardDialog(shell, newLiferayWorkspaceWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.OK) {
			Path parentPath = get(newLiferayWorkspaceOp.getLocation());

			java.nio.file.Path path = Paths.get(parentPath.toOSString());

			String workspaceName = get(newLiferayWorkspaceOp.getWorkspaceName());

			path = path.resolve(workspaceName);

			upgradePlan.setTargetProjectLocation(path);

			setStatus(UpgradePlanElementStatus.COMPLETED);

			IProject project = CoreUtil.getProject(workspaceName);

			_upgradePlanner.dispatch(new UpgradeTaskStepActionPerformedEvent(this, Collections.singletonList(project)));
		}
		else {
			setStatus(UpgradePlanElementStatus.FAILED);

			return UpgradeTasksUIPlugin.createErrorStatus("New Liferay Workspace was not created.");
		}

		return Status.OK_STATUS;
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}