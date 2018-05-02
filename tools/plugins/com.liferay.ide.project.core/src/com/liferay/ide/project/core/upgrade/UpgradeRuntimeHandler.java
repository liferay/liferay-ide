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

package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.AbstractUpgradeProjectHandler;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @author Simon Jiang
 */
public class UpgradeRuntimeHandler extends AbstractUpgradeProjectHandler {

	@Override
	public Status execute(IProject project, String runtimeName, IProgressMonitor monitor, int perUnit) {
		Status retval = Status.createOkStatus();

		try {
			int worked = 0;
			IProgressMonitor submon = CoreUtil.newSubmonitor(monitor, 25);

			submon.subTask("Update project runtime");

			IRuntime runtime = RuntimeManager.getRuntime(runtimeName);

			if (runtime != null) {
				worked = worked + perUnit;

				submon.worked(worked);

				if (runtime != null) {
					IFacetedProject fProject = ProjectUtil.getFacetedProject(project);

					IRuntime primaryRuntime = fProject.getPrimaryRuntime();

					if (!runtime.equals(primaryRuntime)) {
						worked = worked + perUnit;

						submon.worked(worked);

						fProject.setTargetedRuntimes(Collections.singleton(runtime), monitor);

						worked = worked + perUnit;

						submon.worked(worked);

						fProject.setPrimaryRuntime(runtime, monitor);

						worked = worked + perUnit;

						submon.worked(worked);
					}
				}
			}
		}
		catch (Exception e) {
			IStatus error = ProjectCore.createErrorStatus(
				"Unable to upgrade target runtime for " + project.getName(), e);

			ProjectCore.logError(error);

			retval = StatusBridge.create(error);
		}

		return retval;
	}

}