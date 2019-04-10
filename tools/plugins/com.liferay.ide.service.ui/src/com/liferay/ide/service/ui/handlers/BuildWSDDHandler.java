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

package com.liferay.ide.service.ui.handlers;

import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildWSDDJob;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Simon Jiang
 */
public class BuildWSDDHandler extends BuildServiceHandler {

	@Override
	protected IStatus executeServiceBuild(IProject project) {
		IStatus retval = null;

		try {
			new BuildWSDDJob(
				project
			).schedule();
			retval = Status.OK_STATUS;
		}
		catch (Exception e) {
			retval = ServiceCore.createErrorStatus("Unable to execute build-wsdd command", e);
		}

		return retval;
	}

}