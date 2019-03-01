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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.ImportSDKProjectsOp;

import java.nio.file.Path;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ElementList;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {"id=move_legacy_projects", "order=1", "stepId=move_legacy_projects", "title=Move Legacy Projects"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class MoveLegacyProjectsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		final AtomicInteger returnCode = new AtomicInteger();

		ImportSDKProjectsOp sdkProjectsImportOp = ImportSDKProjectsOp.TYPE.instantiate();

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		UIUtil.sync(
			() -> {
				Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

				ImportSDKProjectsWizard importSDKProjectsWizard = new ImportSDKProjectsWizard(
					sdkProjectsImportOp, currentProjectLocation);

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				Shell shell = workbenchWindow.getShell();

				WizardDialog wizardDialog = new WizardDialog(shell, importSDKProjectsWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.OK) {
			ElementList<ProjectNamedItem> projects = sdkProjectsImportOp.getSelectedProjects();

			Stream<ProjectNamedItem> stream = projects.stream();

			stream.map(
				projectNamedItem -> _getter.get(projectNamedItem.getLocation())
			).forEach(
				location -> {

					// TODO Terry will finish the coping next week.

					System.out.println(location);
				}
			);
		}

		return Status.OK_STATUS;
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {};

	@Reference
	private UpgradePlanner _upgradePlanner;

}