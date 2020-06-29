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
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Objects;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Version;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class LiferayRuntimeNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		final NewModuleFragmentOp op = context(NewModuleFragmentOp.class);

		final String runtimeName = get(op.getLiferayRuntimeName());

		IRuntime runtime = ServerUtil.getRuntime(runtimeName);

		if (runtime == null) {
			return Status.createErrorStatus(
				"Please set valid liferay portal runtime, you can initBundle or modify liferay.workspace.bundle.dir " +
					"to make it point to an exsited runtime.");
		}

		IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

		String targetPlatformVersion = liferayWorkspaceProject.getTargetPlatformVersion();

		if (Objects.nonNull(targetPlatformVersion)) {
			PortalRuntime liferayRuntime = (PortalRuntime)runtime.loadAdapter(
				PortalRuntime.class, new NullProgressMonitor());

			if (Objects.isNull(liferayRuntime)) {
				return Status.createErrorStatus("Could not set invalid portal runtime");
			}

			Version workspaceVersion = Version.parseVersion(targetPlatformVersion);

			Version portalRuntimeVersion = Version.parseVersion(liferayRuntime.getPortalVersion());

			int majorWorkspaceVersion = workspaceVersion.getMajor();
			int minorWorkspaceVersion = workspaceVersion.getMinor();
			int majorPortalVersion = portalRuntimeVersion.getMajor();
			int minorPortalVersion = portalRuntimeVersion.getMinor();

			if ((majorWorkspaceVersion != majorPortalVersion) || (minorWorkspaceVersion != minorPortalVersion)) {
				return Status.createErrorStatus(
					"Portal runtime version is not match liferay workspace project version.");
			}
		}

		return retval;
	}

}