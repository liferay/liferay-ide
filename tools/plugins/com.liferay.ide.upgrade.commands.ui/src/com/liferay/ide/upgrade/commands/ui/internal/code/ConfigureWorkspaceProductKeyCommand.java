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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.JobUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.ui.dialog.StringsFilteredDialog;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.code.ConfigureWorkspaceProductKeyCommandKeys;
import com.liferay.ide.upgrade.commands.ui.internal.UpgradeCommandsUIPlugin;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Seiphon Wang
 */
@Component(
	property = "id=" + ConfigureWorkspaceProductKeyCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class ConfigureWorkspaceProductKeyCommand implements UpgradeCommand, UpgradePreview {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		File gradleProperties = _getGradlePropertiesFile();

		if (gradleProperties == null) {
			return Status.CANCEL_STATUS;
		}

		IStatus status = _updateWorkspaceProductKeyValue(gradleProperties);

		return status;
	}

	@Override
	public void preview(IProgressMonitor progressMonitor) {
		File gradeProperties = _getGradlePropertiesFile();

		if (gradeProperties == null) {
			return;
		}

		File tempDir = getTempDir();

		FileUtil.copyFileToDir(gradeProperties, "gradle.properties-preview", tempDir);

		File tempFile = new File(tempDir, "gradle.properties-preview");

		_updateWorkspaceProductKeyValue(tempFile);

		UIUtil.async(
			() -> {
				_upgradeCompare.openCompareEditor(gradeProperties, tempFile);
			});
	}

	private File _getGradlePropertiesFile() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		IProject project = projects.get(0);

		IFile gradeProperties = project.getFile("gradle.properties");

		return FileUtil.getFile(gradeProperties);
	}

	private IStatus _updateWorkspaceProductKeyValue(File gradeProperties) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetVersion = upgradePlan.getTargetVersion();

		try {
			final AtomicInteger returnCode = new AtomicInteger();

			final AtomicReference<String> productKey = new AtomicReference<>();

			UIUtil.async(
				() -> {
					IWorkbench workbench = PlatformUI.getWorkbench();

					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

					Shell shell = workbenchWindow.getShell();

					StringsFilteredDialog dialog = new StringsFilteredDialog(shell);

					dialog.setTitle("Please select a Liferay Product Key:");
					dialog.setMessage("Liferay Product Key Selection");

					List<String> productKeysList = new ArrayList<>();

					Job loadProductJob = new Job("Load workspace product") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								String[] productKeys = BladeCLI.getWorkspaceProducts(true);

								productKeysList.addAll(
									Stream.of(
										productKeys
									).filter(
										key -> key.contains(targetVersion)
									).collect(
										Collectors.toList()
									));
							}
							catch (Exception e) {
							}

							return Status.OK_STATUS;
						}

					};

					loadProductJob.addJobChangeListener(
						new JobChangeAdapter() {

							@Override
							public void done(IJobChangeEvent event) {
								UIUtil.async(
									() -> {
										dialog.setInput(productKeysList.toArray(new String[productKeysList.size()]));

										returnCode.set(dialog.open());

										productKey.set((String)dialog.getFirstResult());

										if (returnCode.get() == Window.OK) {
											try {
												PropertiesConfiguration config = new PropertiesConfiguration(
													gradeProperties);

												config.setProperty(
													WorkspaceConstants.WORKSPACE_PRODUCT_PROPERTY, productKey);

												config.save();
											}
											catch (Exception e) {
											}
										}
									});
							}

						});

					loadProductJob.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

					loadProductJob.setSystem(true);

					loadProductJob.schedule();

					JobUtil.waitForLiferayProjectJob();
				});

			return Status.OK_STATUS;
		}
		catch (Exception e) {
			return UpgradeCommandsUIPlugin.createErrorStatus("Unable to configure worksapce product key", e);
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

}