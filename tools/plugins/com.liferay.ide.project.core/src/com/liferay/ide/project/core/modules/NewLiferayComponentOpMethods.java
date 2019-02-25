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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentOpMethods {

	public static void createNewComponent(NewLiferayComponentOp op, IProgressMonitor monitor) throws CoreException {
		IComponentTemplate<NewLiferayComponentOp> componentOp = _getter.get(op.getComponentClassTemplateName());

		if (componentOp != null) {
			componentOp.doExecute(op, monitor);
		}
	}

	public static final Status execute(NewLiferayComponentOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Creating new Liferay component", 100);

		Status retval = Status.createOkStatus();

		Throwable errorStack = null;

		try {
			createNewComponent(op, monitor);
		}
		catch (Exception e) {
			errorStack = e;
		}

		if ((retval.severity() == Severity.ERROR) && (retval.exception() != null)) {
			errorStack = retval.exception();
		}

		if (errorStack != null) {
			String readableStack = CoreUtil.getStackTrace(errorStack);

			ProjectCore.logError(readableStack);

			return Status.createErrorStatus(readableStack + "\t Please see Eclipse error log for more details.");
		}

		return retval;
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {};

}