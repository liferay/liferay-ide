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

package com.liferay.ide.upgrade.tasks.core.internal.workspace;

import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.tasks.core.FolderSelectionTaskStep;
import com.liferay.ide.upgrade.tasks.core.ProjectImporter;

import java.io.File;
import java.io.IOException;

import java.nio.file.Paths;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=import_liferay_workspace", "requirement=required", "order=1", "taskId=migrate_workspace",
		"title=Import Liferay Workspace"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class ImportWorkspaceTaskStep extends FolderSelectionTaskStep {

	@Override
	public IStatus execute(File folder, IProgressMonitor progressMonitor) {
		Job importJob = new Job("Importing project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IPath location = new Path(folder.getCanonicalPath());

					_projectImporter.importProjects(Paths.get(location.toOSString()));
				}
				catch (IOException ioe) {
				}

				return Status.OK_STATUS;
			}

		};

		importJob.schedule();

		return Status.OK_STATUS;
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

	@Reference
	private ProjectImporter _projectImporter;

}