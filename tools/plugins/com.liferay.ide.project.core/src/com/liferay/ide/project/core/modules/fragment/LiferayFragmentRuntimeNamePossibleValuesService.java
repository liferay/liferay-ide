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
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;

import java.util.Objects;
import java.util.Set;

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
public class LiferayFragmentRuntimeNamePossibleValuesService
	extends LiferayRuntimeNamePossibleValuesService implements IRuntimeLifecycleListener, SapphireContentAccessor {

	@Override
	protected void compute(Set<String> values) {
		IRuntime[] runtimes = ServerCore.getRuntimes();

		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();
		NewModuleFragmentFilesOp op = context(NewModuleFragmentFilesOp.class);

		if (ListUtil.isNotEmpty(runtimes)) {
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
					return;
				}

				if (workspaceProject != null) {
					IPath bundleHomePath = LiferayWorkspaceUtil.getBundleHomePath(workspaceProject);

					if (Objects.isNull(bundleHomePath)) {
						continue;
					}

					if (bundleHomePath.equals(runtime.getLocation())) {
						values.add(runtime.getName());
					}
				}
				else {
					values.add(runtime.getName());
				}
			}
		}
	}

}