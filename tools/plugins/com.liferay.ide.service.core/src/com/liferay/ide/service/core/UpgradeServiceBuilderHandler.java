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

package com.liferay.ide.service.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.AbstractUpgradeProjectHandler;
import com.liferay.ide.service.core.job.BuildServiceJob;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class UpgradeServiceBuilderHandler extends AbstractUpgradeProjectHandler {

	@Override
	public Status execute(IProject project, String runtimeName, IProgressMonitor monitor, int perUnit) {
		Status retval = Status.createOkStatus();

		try {
			int worked = 0;

			IProgressMonitor submon = CoreUtil.newSubmonitor(monitor, 25);

			submon.subTask("Executing build-service for " + project.getName());

			worked = worked + perUnit;

			submon.worked(worked);

			BuildServiceJob job = new BuildServiceJob(project);

			job.schedule();
			job.join();

			IStatus result = job.getResult();

			if (!result.isOK()) {
				throw new CoreException(result);
			}

			worked = worked + perUnit;

			submon.worked(worked);
		}
		catch (Exception e) {
			IStatus error = ServiceCore.createErrorStatus(
				"Unable to run service build task for " + project.getName(), e);
			ServiceCore.logError("Unable to run service build task for " + project.getName(), e);

			retval = StatusBridge.create(error);
		}

		return retval;
	}

}