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

package com.liferay.ide.service.core.job;

import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.service.core.ServiceCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class BuildWSDDJob extends BuildServiceJob {

	public BuildWSDDJob(IProject project) {
		super(project);

		setName(Msgs.buildWSDD);
		setUser(true);
	}

	@Override
	protected void runBuild(IProgressMonitor monitor) throws CoreException {
		IProjectBuilder builder = getProjectBuilder();

		monitor.worked(50);

		IStatus retval = builder.buildWSDD(monitor);

		if (retval == null) {
			retval = ServiceCore.createErrorStatus(NLS.bind(Msgs.errorRunningBuildWSDD, getProject()));
		}

		if ((retval == null) || !retval.isOK()) {
			throw new CoreException(retval);
		}

		monitor.worked(90);
	}

	private static class Msgs extends NLS {

		public static String buildWSDD;
		public static String errorRunningBuildWSDD;

		static {
			initializeMessages(BuildWSDDJob.class.getName(), Msgs.class);
		}

	}

}