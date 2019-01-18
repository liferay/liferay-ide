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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.jobs.JobUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ImportLiferayWorkspaceOpMethods {

	public static Status execute(ImportLiferayWorkspaceOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Importing Liferay Workspace project...", 100);

		Status retval = Status.createOkStatus();

		try {
			Value<String> buildTypeValue = op.getBuildType();

			String buildType = buildTypeValue.content();

			op.setProjectProvider(buildType);

			Value<NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp>> projectProvider = op.getProjectProvider();

			NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> provider = projectProvider.content(true);

			Value<Path> locationValue = op.getWorkspaceLocation();

			Path workspaceLocation = locationValue.content();

			String location = workspaceLocation.toOSString();

			String workspaceName = workspaceLocation.lastSegment();

			LiferayWorkspaceUtil.clearWorkspace(location);

			IStatus importStatus = provider.importProject(PathBridge.create(workspaceLocation), monitor);

			if (importStatus != org.eclipse.core.runtime.Status.OK_STATUS) {
				return StatusBridge.create(importStatus);
			}

			Value<Boolean> provisionLiferayBundle = op.getProvisionLiferayBundle();

			boolean initBundle = provisionLiferayBundle.content();

			Value<Boolean> hasRuntimeDir = op.getHasBundlesDir();

			boolean hasBundlesDir = hasRuntimeDir.content();

			Value<String> serverNameValue = op.getServerName();

			String serverName = serverNameValue.content();

			if (initBundle && !hasBundlesDir) {
				Value<String> bundleUrl = op.getBundleUrl();

				provider.initBundle(bundleUrl.content(false), serverName, workspaceName);
			}

			if (!initBundle && hasBundlesDir) {
				JobUtil.waitForLiferayProjectJob();

				Job addPortalRuntimeJob = new Job("Add Liferay Portal Runtime Job") {

					@Override
					public boolean belongsTo(Object family) {
						return family.equals(LiferayCore.LIFERAY_JOB_FAMILY);
					}

					protected IStatus run(IProgressMonitor monitor) {
						LiferayWorkspaceUtil.addPortalRuntime(serverName);

						return org.eclipse.core.runtime.Status.OK_STATUS;
					}

				};

				addPortalRuntimeJob.join();
				addPortalRuntimeJob.schedule();
			}
		}
		catch (Exception e) {
			retval = Status.createErrorStatus("Importing Liferay Workspace Project failed", e);
		}

		return retval;
	}

}