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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;

import java.util.Objects;

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

		NewModuleFragmentFilesOp op = context(NewModuleFragmentFilesOp.class);

		IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

		String value = _NONE;

		for (IRuntime runtime : runtimes) {
			if (LiferayServerCore.newPortalBundle(runtime.getLocation()) == null) {
				continue;
			}

			String portalBundleVersion = NewModuleFragmentFilesOpMethods.getFragmentPortalBundleVersion(op);

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

			if (liferayWorkspaceProject != null) {
				IPath bundleHomePath = LiferayWorkspaceUtil.getBundleHomePath(liferayWorkspaceProject.getProject());

				if (Objects.isNull(bundleHomePath)) {
					continue;
				}

				IPath runtimeLocation = runtime.getLocation();

				if (bundleHomePath.equals(runtimeLocation)) {
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

	private static final String _NONE = "<None>";

}