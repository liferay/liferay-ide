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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.Synchronizer;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.ImportSDKProjectsOp;
import com.liferay.ide.upgrade.tasks.core.sdk.MoveLegacyProjectsStepKeys;

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
		"id=migrate_legacy_projects_to_wars", "order=2", "stepId=" + MoveLegacyProjectsStepKeys.ID,
		"title=Migrate Legacy Projects To Wars"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class MigrateLegacyProjectsToWarsAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (targetProjectLocation == null) {
			return UpgradeTasksUIPlugin.createErrorStatus("There is no target project configured for current plan.");
		}

		Path pluginsSDKLoaction = upgradePlan.getCurrentProjectLocation();

		if (FileUtil.notExists(pluginsSDKLoaction.toFile())) {
			return UpgradeTasksUIPlugin.createErrorStatus("There is no plugins sdk folder in " + pluginsSDKLoaction);
		}

		final AtomicInteger returnCode = new AtomicInteger();

		ImportSDKProjectsOp sdkProjectsImportOp = ImportSDKProjectsOp.TYPE.instantiate();

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
					StringBuilder sb = new StringBuilder();

					org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(location.toString());

					String name = path.lastSegment();

					sb.append("convert ");
					sb.append("--source \"");
					sb.append(pluginsSDKLoaction.toString());
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
	private Synchronizer _synchronizer;

	@Reference
	private UpgradePlanner _upgradePlanner;

}