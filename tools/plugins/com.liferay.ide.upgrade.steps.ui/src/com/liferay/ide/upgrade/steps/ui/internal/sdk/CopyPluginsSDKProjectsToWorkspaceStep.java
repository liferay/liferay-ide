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

package com.liferay.ide.upgrade.steps.ui.internal.sdk;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStepPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeStepStatus;
import com.liferay.ide.upgrade.steps.core.ImportSDKProjectsOp;
import com.liferay.ide.upgrade.steps.core.sdk.MigratePluginsSDKProjectsStepKeys;
import com.liferay.ide.upgrade.steps.ui.internal.ImportSDKProjectsWizard;
import com.liferay.ide.upgrade.steps.ui.internal.UpgradeStepsUIPlugin;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
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
 * @author Gregory Amerson
 */
@Component(
	property = {
		"description=" + CopyPluginsSDKProjectsToWorkspaceStepKeys.DESCRIPTION, "imagePath=icons/copy_to_workspace.png",
		"id=" + CopyPluginsSDKProjectsToWorkspaceStepKeys.ID, "order=3", "requirement=required",
		"parentId=" + MigratePluginsSDKProjectsStepKeys.ID, "title=" + CopyPluginsSDKProjectsToWorkspaceStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class CopyPluginsSDKProjectsToWorkspaceStep extends BaseUpgradeStep implements SapphireContentAccessor {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

		Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

		if (currentProjectLocation == null) {
			return UpgradeStepsUIPlugin.createErrorStatus(
				"There is no current project location configured for current plan.");
		}

		Path pluginsSDKLoaction = targetProjectLocation.resolve("plugins-sdk");

		if (FileUtil.notExists(pluginsSDKLoaction.toFile())) {
			return UpgradeStepsUIPlugin.createErrorStatus("There is no plugins-sdk folder in " + targetProjectLocation);
		}

		final AtomicInteger returnCode = new AtomicInteger();

		ImportSDKProjectsOp sdkProjectsImportOp = ImportSDKProjectsOp.TYPE.instantiate();

		UIUtil.sync(
			() -> {
				ImportSDKProjectsWizard importSDKProjectsWizard = new ImportSDKProjectsWizard(
					sdkProjectsImportOp, currentProjectLocation);

				IWorkbench workbench = PlatformUI.getWorkbench();

				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

				Shell shell = workbenchWindow.getShell();

				WizardDialog wizardDialog = new WizardDialog(shell, importSDKProjectsWizard);

				returnCode.set(wizardDialog.open());
			});

		if (returnCode.get() == Window.OK) {
			ElementList<ProjectNamedItem> projectNamedItems = sdkProjectsImportOp.getSelectedProjects();

			Stream<ProjectNamedItem> stream = projectNamedItems.stream();

			List<IProject> projects = stream.map(
				projectNamedItem -> get(projectNamedItem.getLocation())
			).map(
				location -> Paths.get(location)
			).map(
				source -> {
					int beginIndex = source.getNameCount() - 2;
					int endIndex = source.getNameCount();

					Path subpath = source.subpath(beginIndex, endIndex);

					Path newLocation = pluginsSDKLoaction.resolve(subpath);

					File sourceFile = source.toFile();

					try {
						FileUtils.copyDirectory(sourceFile, newLocation.toFile());
					}
					catch (IOException ioe) {
						UpgradeStepsUIPlugin.logError(
							"Copy project " + source + " failed, please clear the folder and try again", ioe);
					}

					org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(newLocation.toString());

					IProject newProject = null;

					try {
						newProject = CoreUtil.openProject(sourceFile.getName(), path, progressMonitor);

						_addNaturesToProject(newProject, JavaCore.NATURE_ID, progressMonitor);
					}
					catch (CoreException ce) {
					}

					return newProject;
				}
			).filter(
				Objects::nonNull
			).collect(
				Collectors.toList()
			);

			setStatus(UpgradeStepStatus.COMPLETED);

			_upgradePlanner.dispatch(new UpgradeStepPerformedEvent(this, projects));
		}

		return Status.OK_STATUS;
	}

	private void _addNaturesToProject(IProject project, String natureId, IProgressMonitor monitor)
		throws CoreException {

		IProjectDescription description = project.getDescription();

		String[] prevNatures = description.getNatureIds();

		String[] newNatures = new String[prevNatures.length + 1];

		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		newNatures[newNatures.length - 1] = natureId;

		description.setNatureIds(newNatures);

		project.setDescription(description, monitor);
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}