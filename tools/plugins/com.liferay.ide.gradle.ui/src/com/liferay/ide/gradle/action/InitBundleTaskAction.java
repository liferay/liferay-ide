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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Terry Jia
 */
public class InitBundleTaskAction extends GradleTaskAction {

	protected void afterTask() {
		IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

		IPath bundlesLocation = LiferayWorkspaceUtil.getHomeLocation(project);

		try {
			if (bundlesLocation.toFile().exists()) {
				String serverName = bundlesLocation.lastSegment();

				ServerUtil.addPortalRuntimeAndServer(serverName, bundlesLocation, new NullProgressMonitor());

				IProject pluginsSDK = CoreUtil.getProject(
					LiferayWorkspaceUtil.getPluginsSDKDir(project.getLocation().toPortableString()));

				if ((pluginsSDK != null) && pluginsSDK.exists()) {
					SDK sdk = SDKUtil.createSDKFromLocation(pluginsSDK.getLocation());

					sdk.addOrUpdateServerProperties(
						ServerUtil.getLiferayRuntime(ServerUtil.getServer(serverName)).getLiferayHome());

					pluginsSDK.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			}
		}
		catch (Exception e) {
			GradleCore.logError("Adding server failed", e);
		}
	}

	@Override
	protected String getGradleTask() {
		return "initBundle";
	}

}