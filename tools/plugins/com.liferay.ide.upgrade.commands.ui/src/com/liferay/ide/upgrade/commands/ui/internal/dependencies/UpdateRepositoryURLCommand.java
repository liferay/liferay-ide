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

package com.liferay.ide.upgrade.commands.ui.internal.dependencies;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.ui.internal.UpgradeCommandsUIPlugin;
import com.liferay.ide.upgrade.plan.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = "id=" + UpdateRepositoryURLCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class UpdateRepositoryURLCommand implements UpgradeCommand, UpgradePreview {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		File settingsGradle = _getSettingsGradleFile();

		if (settingsGradle == null) {
			return Status.CANCEL_STATUS;
		}

		IStatus status = _updateRepositoryURL(settingsGradle);

		if (status.isOK()) {
			_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, Collections.singletonList(settingsGradle)));
		}

		return status;
	}

	@Override
	public void preview(IProgressMonitor progressMonitor) {
		File settingsGradle = _getSettingsGradleFile();

		if (settingsGradle == null) {
			return;
		}

		File tempDir = getTempDir();

		FileUtil.copyFileToDir(settingsGradle, "settings.gradle-preview", tempDir);

		File tempFile = new File(tempDir, "settings.gradle-preview");

		IStatus status = _updateRepositoryURL(tempFile);

		if (status.isOK()) {
			UIUtil.async(
				() -> {
					_upgradeCompare.openCompareEditor(settingsGradle, tempFile);
				});
		}
	}

	private File _getSettingsGradleFile() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Liferay Workspace Project", false, ResourceSelection.WORKSPACE_PROJECTS);

		if (projects.isEmpty()) {
			return null;
		}

		IProject project = projects.get(0);

		return FileUtil.getFile(project.getFile("settings.gradle"));
	}

	private IStatus _updateRepositoryURL(File settingsGradle) {
		String contents = FileUtil.readContents(settingsGradle, true);

		String oldRepositoryURLPrefix = "https://cdn.lfrs.sl/repository.liferay.com/";

		if (contents.contains(oldRepositoryURLPrefix)) {
			contents = contents.replaceAll(oldRepositoryURLPrefix, "https://repository-cdn.liferay.com/");

			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"))) {
				FileUtil.writeFile(settingsGradle, byteArrayInputStream);
			}
			catch (Exception e) {
				return UpgradeCommandsUIPlugin.createErrorStatus("Update repository url failed", e);
			}
		}
		else {
			_messagePrompt.promptInfo("No need to update", "There is no need to update for this project.");

			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradeCompare _upgradeCompare;

	@Reference
	private UpgradePlanner _upgradePlanner;

}