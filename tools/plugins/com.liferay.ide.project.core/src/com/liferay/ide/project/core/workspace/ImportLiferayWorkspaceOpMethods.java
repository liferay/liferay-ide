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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 */
public class ImportLiferayWorkspaceOpMethods {

	public static Status execute(ImportLiferayWorkspaceOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Importing Liferay Workspace project...", 100);

		Status retval = null;

		try {
			op.setProjectProvider(op.getBuildType().content());

			NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> provider = op.getProjectProvider().content(true);

			org.eclipse.sapphire.modeling.Path workspaceLocation = op.getWorkspaceLocation().content();

			String location = workspaceLocation.toOSString();

			LiferayWorkspaceUtil.clearWorkspace(location);

			boolean initBundle = op.getProvisionLiferayBundle().content();
			boolean hasBundlesDir = op.getHasBundlesDir().content();
			String bundleUrl = op.getBundleUrl().content(false);

			IStatus importStatus;

			if (initBundle && !hasBundlesDir) {
				importStatus = provider.importProject(location, monitor, true, bundleUrl);
			}
			else {
				importStatus = provider.importProject(location, monitor, false, null);
			}

			retval = StatusBridge.create(importStatus);

			if (!retval.ok() || (retval.exception() != null)) {
				return retval;
			}
		}
		catch (Exception e) {
			String msg = "import Liferay Workspace project error";

			ProjectCore.logError(msg, e);

			retval = Status.createErrorStatus(msg, e);
		}

		return retval;
	}

}