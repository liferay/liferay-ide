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

package com.liferay.ide.project.core.model;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
public class ParentSDKProjectImportOpMethods {

	public static final Status execute(ParentSDKProjectImportOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Importing Liferay parent sdk project...", 100);

		Status retval = Status.createOkStatus();

		Path sdkLocation = _getter.get(op.getSdkLocation());

		if ((sdkLocation == null) || sdkLocation.isEmpty()) {
			return Status.createErrorStatus("SDK folder cannot be empty");
		}

		SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkLocation));

		try {
			SDKUtil.openAsProject(sdk, monitor);
		}
		catch (CoreException ce) {
			retval = StatusBridge.create(ProjectCore.createErrorStatus(ce));
		}

		return retval;
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}