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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;

import java.util.Map;
import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class LiferayFragmentRuntimeNameDefaultValueService
	extends LiferayRuntimeNameDefaultValueService implements IRuntimeLifecycleListener {

	@Override
	protected String compute() {
		IRuntime[] runtimes = ServerCore.getRuntimes();

		if (ListUtil.isEmpty(runtimes)) {
			return _NONE;
		}

		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();
		String value = _NONE;

		for (IRuntime runtime : runtimes) {
			if (LiferayServerCore.newPortalBundle(runtime.getLocation()) == null) {
				continue;
			}

			String portalBundleVersion = _getFragmentPortalBundleVersion();

			if (CoreUtil.isNotNullOrEmpty(portalBundleVersion)) {
				PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(
					PortalRuntime.class, new NullProgressMonitor());

				if (!Objects.equals(portalBundleVersion, portalRuntime.getPortalVersion())) {
					continue;
				}
			}
			else {
				break;
			}

			NewModuleFragmentFilesOp op = context(NewModuleFragmentFilesOp.class);

			String projectName = get(op.getProjectName());

			IProject project = CoreUtil.getProject(projectName);

			IPath projectLocation = project.getLocation();

			if (workspaceProject != null) {
				IPath workspaceProjectLocation = workspaceProject.getLocation();

				if (workspaceProjectLocation.isPrefixOf(projectLocation)) {
					String homeDir = LiferayWorkspaceUtil.getHomeDir(workspaceProject);

					IPath wsProjectLocation = workspaceProject.getLocation();

					IPath bundleLocation = wsProjectLocation.append(homeDir);

					IPath runtimeLocation = runtime.getLocation();

					if (bundleLocation.isPrefixOf(runtimeLocation)) {
						value = runtime.getName();

						break;
					}
				}
				else {
					value = runtime.getName();

					break;
				}
			}
			else {
				value = runtime.getName();

				break;
			}
		}

		return value;
	}

	private String _getFragmentPortalBundleVersion() {
		NewModuleFragmentFilesOp op = context(NewModuleFragmentFilesOp.class);

		String projectName = get(op.getProjectName());

		IProject project = CoreUtil.getProject(projectName);

		if (project == null) {
			return null;
		}

		Map<String, String> fragmentProjectInfo = ProjectUtil.getFragmentProjectInfo(project);

		if (fragmentProjectInfo != null) {
			return fragmentProjectInfo.get("Portal-Bundle-Version");
		}

		return null;
	}

	private static final String _NONE = "<None>";

}