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

package com.liferay.ide.upgrade.tasks.core.internal.sdk;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.tasks.core.ProjectImporter;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;

import java.nio.file.Path;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"id=import_plugins_sdk", "order=1", "stepId=" + ImportExistingPluginsSDKStepKeys.ID, "title=Import Plugins SDK"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class ImportPluginsSDKAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		Path rootProjectPath = _projectSelection.selectPath("Please select the root folder of you Liferay Plugins SDK");

		if (rootProjectPath == null) {
			return Status.CANCEL_STATUS;
		}

		return _projectImporter.importProjects(rootProjectPath, progressMonitor);
	}

	@Reference(target = "(type=plugins_sdk)")
	private ProjectImporter _projectImporter;

	@Reference
	private ResourceSelection _projectSelection;

}