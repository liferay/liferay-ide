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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Charles Wu
 */
public class NewModuleExtOpMethods {

	public static final Status execute(NewModuleExtOp newModuleExtOp, ProgressMonitor pm) {
		IProgressMonitor progressMonitor = ProgressMonitorBridge.create(pm);

		progressMonitor.beginTask("Creating Liferay module ext project template files", 100);

		Status retval = null;

		Throwable errorStack = null;

		try {
			NewLiferayProjectProvider<BaseModuleOp> newLiferayProjectProvider = SapphireUtil.getContent(
				newModuleExtOp.getProjectProvider());

			IStatus status = newLiferayProjectProvider.createNewProject(newModuleExtOp, progressMonitor);

			retval = StatusBridge.create(status);

			if ((retval.severity() == Severity.ERROR) && (retval.exception() != null)) {
				errorStack = retval.exception();
			}
		}
		catch (Exception e) {
			errorStack = e;
		}

		if (errorStack != null) {
			String readableStack = CoreUtil.getStackTrace(errorStack);

			ProjectCore.logError(readableStack);

			return Status.createErrorStatus(readableStack + "\t Please see Eclipse error log for more details.");
		}

		return retval;
	}

}