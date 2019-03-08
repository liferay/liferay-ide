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

package com.liferay.ide.upgrade.tasks.ui.internal.sdk;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectSynchronizer;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.tasks.core.ImportSDKProjectsOp;
import com.liferay.ide.upgrade.tasks.core.sdk.MigratePluginsSDKTaskKeys;
import com.liferay.ide.upgrade.tasks.ui.internal.ImportSDKProjectsWizard;
import com.liferay.ide.upgrade.tasks.ui.internal.UpgradeTasksUIPlugin;

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
	property = {
		"description=" + MigratePluginsSDKProjectsToWorkspaceStepKeys.DESCRIPTION,
		"id=" + MigratePluginsSDKProjectsToWorkspaceStepKeys.ID, "imagePath=icons/export.png",
		"requirement=recommended", "order=2", "taskId=" + MigratePluginsSDKTaskKeys.ID,
		"title=" + MigratePluginsSDKProjectsToWorkspaceStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class MigratePluginsSDKProjectsToWorkspaceStep extends BaseUpgradeTaskStep {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (targetProjectLocation == null) {
			return UpgradeTasksUIPlugin.createErrorStatus("There is no target project configured for current plan.");
		}

		Path workspaceLocation = upgradePlan.getCurrentProjectLocation();

		if (FileUtil.notExists(workspaceLocation.toFile())) {
			return UpgradeTasksUIPlugin.createErrorStatus("There is no code located at " + workspaceLocation);
		}

		Path legacyPluginsSDKPath = workspaceLocation.resolve("plugins-sdk");

		final AtomicInteger returnCode = new AtomicInteger();

		ImportSDKProjectsOp sdkProjectsImportOp = ImportSDKProjectsOp.TYPE.instantiate();

		UIUtil.sync(
			() -> {
				ImportSDKProjectsWizard importSDKProjectsWizard = new ImportSDKProjectsWizard(
					sdkProjectsImportOp, legacyPluginsSDKPath);

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
					StringBuilder sb = new StringBuilder();

					org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(location.toString());

					String name = path.lastSegment();

					sb.append("convert ");
					sb.append("--source \"");
					sb.append(legacyPluginsSDKPath.toString());
					sb.append("\" --base \"");
					sb.append(targetProjectLocation.toString());
					sb.append("\" \"");
					sb.append(name);
					sb.append("\"");

					try {
						BladeCLI.execute(sb.toString());
					}
					catch (Exception e) {
						UpgradeTasksUIPlugin.logError("Convert failed on project " + name, e);
					}
				}
			);

			org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(targetProjectLocation.toString());

			_synchronizer.synchronizePath(path, progressMonitor);
		}

		return Status.OK_STATUS;
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {};

	@Reference(target = "(type=gradle)")
	private ProjectSynchronizer _synchronizer;

	@Reference
	private UpgradePlanner _upgradePlanner;

}