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

import com.liferay.ide.core.util.FileUtil;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

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

		if (Objects.isNull(gradleProperties)) {
			return Status.CANCEL_STATUS;
		}

		return _updateWorkspaceProductKeyValue(gradleProperties);
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

		UIUtil.async(() -> _upgradeCompare.openCompareEditor(gradeProperties, tempFile));
	}

	public class AsyncStringsSelectionValidator implements ISelectionStatusValidator {

		public AsyncStringsSelectionValidator(boolean multiSelect) {
		}

		public IStatus validate(Object[] selection) {
			if ((selection != null) && (selection.length > 0)) {
				String selectionItem = (String)selection[0];

				if (Objects.equals("Loading Data......", selectionItem)) {
					return new Status(IStatus.ERROR, "unknown", 1, "", null);
				}
			}
			else {
				return new Status(IStatus.ERROR, "unknown", 1, "", null);
			}

			return Status.OK_STATUS;
		}

	}

	private File _getGradlePropertiesFile() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		IProject project = projects.get(0);

		return FileUtil.getFile(project.getFile("gradle.properties"));
	}

	private void _loadWorkspaceProduct(TreeViewer viewer, String targetPlatformVersion) {
		try {
			UIUtil.async(
				() -> {
					if (viewer == null) {
						return;
					}

					Tree tree = viewer.getTree();

					if (tree.isDisposed()) {
						return;
					}

					try {
						String[] filterProductKeys = Stream.of(
							BladeCLI.getWorkspaceProducts(true)
						).filter(
							key -> key.contains(targetPlatformVersion)
						).collect(
							Collectors.toList()
						).toArray(
							new String[0]
						);

						viewer.setInput(filterProductKeys);
					}
					catch (Exception e) {
						UpgradeCommandsUIPlugin.logError("Failed to load workspace product keys", e);
					}
				});
		}
		catch (Exception e) {
			UpgradeCommandsUIPlugin.logError(e.getMessage());
		}
	}

	private IStatus _updateWorkspaceProductKeyValue(File gradeProperties) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		String targetVersion = upgradePlan.getTargetVersion();

		try {
			final AtomicInteger returnCode = new AtomicInteger();

			final AtomicReference<String> productKey = new AtomicReference<>();

			UIUtil.sync(
				() -> {
					IWorkbench workbench = PlatformUI.getWorkbench();

					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

					AsyncStringFilteredDialog dialog = new AsyncStringFilteredDialog(
						workbenchWindow.getShell(), targetVersion);

					dialog.setInput(new String[] {"Loading Data......"});
					dialog.setMessage("Liferay Product Key Selection");
					dialog.setTitle("Please select a Liferay Product Key:");
					dialog.setStatusLineAboveButtons(false);
					dialog.setHelpAvailable(false);

					returnCode.set(dialog.open());
					productKey.set((String)dialog.getFirstResult());
				});

			if (returnCode.get() == Window.OK) {
				try {
					PropertiesConfiguration config = new PropertiesConfiguration(gradeProperties);

					config.setProperty(WorkspaceConstants.WORKSPACE_PRODUCT_PROPERTY, productKey);

					config.save();
				}
				catch (Exception e) {
					return UpgradeCommandsUIPlugin.createErrorStatus("Unable to save workspace product key", e);
				}
			}

			return Status.OK_STATUS;
		}
		catch (Exception e) {
			return UpgradeCommandsUIPlugin.createErrorStatus("Unable to configure workspace product key", e);
		}
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

	@SuppressWarnings("restriction")
	private class AsyncStringFilteredDialog extends StringsFilteredDialog {

		public AsyncStringFilteredDialog(Shell shell, String targetPlatforVersion) {
			super(shell, null);

			setValidator(new AsyncStringsSelectionValidator(false));

			_targetPlatformVersion = targetPlatforVersion;
		}

		@Override
		protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
			TreeViewer treeViewer = super.doCreateTreeViewer(parent, style);

			_loadWorkspaceProduct(treeViewer, _targetPlatformVersion);

			return treeViewer;
		}

		@Override
		protected void updateStatus(IStatus status) {
			updateButtonsEnableState(status);
		}

		private String _targetPlatformVersion;

	}

}