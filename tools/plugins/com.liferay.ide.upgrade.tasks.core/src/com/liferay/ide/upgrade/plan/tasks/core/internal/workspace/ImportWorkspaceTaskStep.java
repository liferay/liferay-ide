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

package com.liferay.ide.upgrade.plan.tasks.core.internal.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.plan.tasks.core.FolderSelectionTaskStep;
import com.liferay.ide.upgrade.plan.tasks.core.internal.workspace.importer.ImportedProjectImporter;
import com.liferay.ide.upgrade.plan.tasks.core.internal.workspace.importer.Importer;
import com.liferay.ide.upgrade.plan.tasks.core.internal.workspace.importer.LiferayWorkspaceGradleImporter;
import com.liferay.ide.upgrade.plan.tasks.core.internal.workspace.importer.LiferayWorkspaceGradleWithSDKImporter;

import java.io.File;
import java.io.IOException;

import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=import_liferay_workspace", "requirement=required", "order=100", "taskId=migrate_workspace",
		"title=Import Liferay Workspace"
	},
	service = UpgradeTaskStep.class
)
public class ImportWorkspaceTaskStep extends FolderSelectionTaskStep {

	@Override
	public IStatus execute(File folder, IProgressMonitor progressMonitor) {
		Job importJob = new Job("Importing project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IPath location = new Path(folder.getCanonicalPath());

					Importer importer = null;

					if (_isAlreadyImported(location)) {
						importer = new ImportedProjectImporter(location);
					}
					else if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(location)) {
						importer = new LiferayWorkspaceGradleImporter(location);
					}
					else if (SDKUtil.isValidSDKLocation(location)) {
						importer = new LiferayWorkspaceGradleWithSDKImporter(location);
					}
					else {
						return Status.CANCEL_STATUS;
					}

					importer.doBefore(monitor);

					importer.doImport(monitor);
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

	private boolean _isAlreadyImported(IPath path) {
		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		IContainer[] containers = workspaceRoot.findContainersForLocationURI(FileUtil.toURI(path));

		long projectCount = Stream.of(
			containers
		).filter(
			container -> container instanceof IProject
		).count();

		if (projectCount > 0) {
			return true;
		}

		return false;
	}

}