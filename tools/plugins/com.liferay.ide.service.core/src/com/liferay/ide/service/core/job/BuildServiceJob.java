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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.service.core.ServiceCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class BuildServiceJob extends Job {

	public BuildServiceJob(IProject project) {
		super(Msgs.buildServices);

		this.project = project;

		setUser(true);
	}

	protected IProject getProject() {
		return project;
	}

	protected IProjectBuilder getProjectBuilder() throws CoreException {
		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, getProject());

		if (liferayProject == null) {
			throw new CoreException(
				ServiceCore.createErrorStatus(NLS.bind(Msgs.couldNotCreateLiferayProject, getProject())));
		}

		IProjectBuilder builder = liferayProject.adapt(IProjectBuilder.class);

		if (builder == null) {
			throw new CoreException(
				ServiceCore.createErrorStatus(NLS.bind(Msgs.couldNotCreateProjectBuilder, getProject())));
		}

		return builder;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus retval = null;

		if (getProject() == null) {
			return ServiceCore.createErrorStatus(Msgs.useLiferayProjectImportWizard);
		}

		monitor.beginTask(Msgs.buildingLiferayServices, 100);

		try {
			runBuild(monitor);
		}
		catch (CoreException ce) {
			retval = ServiceCore.createErrorStatus(ce);
		}

		if ((retval == null) || retval.isOK()) {
			return Status.OK_STATUS;
		}

		return retval;
	}

	protected void runBuild(IProgressMonitor monitor) throws CoreException {
		IProjectBuilder builder = getProjectBuilder();

		monitor.worked(50);

		IStatus retval = builder.buildService(monitor);

		if (retval == null) {
			retval = ServiceCore.createErrorStatus(NLS.bind(Msgs.errorRunningBuildService, getProject()));
		}

		if ((retval == null) || !retval.isOK()) {
			throw new CoreException(retval);
		}

		monitor.worked(90);
	}

	protected IProject project;

	protected static class Msgs extends NLS {

		public static String buildingLiferayServices;
		public static String buildServices;
		public static String couldNotCreateLiferayProject;
		public static String couldNotCreateProjectBuilder;
		public static String errorRunningBuildService;
		public static String useConvertLiferayProject;
		public static String useLiferayProjectImportWizard;

		static {
			initializeMessages(BuildServiceJob.class.getName(), Msgs.class);
		}

	}

}