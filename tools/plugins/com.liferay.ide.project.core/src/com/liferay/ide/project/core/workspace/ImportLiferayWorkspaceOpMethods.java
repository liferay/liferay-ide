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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 * @author Terry Jia
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

			LiferayWorkspaceUtil.clearWorkspace(location);

			IStatus status = provider.importProject(location, monitor);

			retval = StatusBridge.create(status);

			if (retval.ok()) {
				Value<Boolean> provisionLiferayBundle = op.getProvisionLiferayBundle();

				boolean initBundle = provisionLiferayBundle.content();

				Value<Boolean> hasRuntimeDir = op.getHasBundlesDir();

				boolean hasBundlesDir = hasRuntimeDir.content();

				Value<String> serverNameValue = op.getServerName();

				String serverName = serverNameValue.content();

				if (initBundle && !hasBundlesDir) {
					Value<String> bundleUrl = op.getBundleUrl();

					String workspaceName = workspaceLocation.lastSegment();

					provider.initBundle(bundleUrl.content(), serverName, workspaceName, monitor);
				}

				if (initBundle || hasBundlesDir) {
					IPath bundlesLocation = null;

					if (buildType.equals("gradle-liferay-workspace")) {
						bundlesLocation = LiferayWorkspaceUtil.getHomeLocation(location);
					}
					else {
						bundlesLocation = new org.eclipse.core.runtime.Path(location).append("bundles");
					}

					if (FileUtil.exists(bundlesLocation)) {
						ServerUtil.addPortalRuntimeAndServer(serverName, bundlesLocation, monitor);
					}
				}
			}
		}
		catch (Exception e) {
			retval = Status.createErrorStatus("Importing Liferay Workspace Project failed", e);
		}

		return retval;
	}

}