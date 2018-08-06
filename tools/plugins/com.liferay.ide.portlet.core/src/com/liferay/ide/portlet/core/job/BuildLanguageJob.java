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

package com.liferay.ide.portlet.core.job;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.project.core.IProjectBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class BuildLanguageJob extends Job {

	public BuildLanguageJob(IFile langFile) {
		super(Msgs.buildLanguages);

		this.langFile = langFile;
		setUser(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus retval = null;

		IWorkspace workspace = CoreUtil.getWorkspace();

		IWorkspaceDescription desc = workspace.getDescription();

		boolean saveAutoBuild = desc.isAutoBuilding();

		desc.setAutoBuilding(false);

		monitor.beginTask(Msgs.buildingLanguages, 100);

		IWorkspaceRunnable workspaceRunner = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				runBuildLang(monitor);
			}

		};

		try {
			workspace.setDescription(desc);

			workspace.run(workspaceRunner, monitor);
		}
		catch (CoreException ce) {
			retval = PortletCore.createErrorStatus(ce);
		}
		finally {
			desc = workspace.getDescription();

			desc.setAutoBuilding(saveAutoBuild);

			try {
				workspace.setDescription(desc);
			}
			catch (CoreException ce) {
				retval = PortletCore.createErrorStatus(ce);
			}
		}

		if ((retval == null) || retval.isOK()) {
			return Status.OK_STATUS;
		}

		return retval;
	}

	protected void runBuildLang(IProgressMonitor monitor) throws CoreException {
		ILiferayProject liferayProject = LiferayCore.create(_getProject());

		if (liferayProject == null) {
			throw new CoreException(
				PortletCore.createErrorStatus(NLS.bind(Msgs.couldNotCreateLiferayProject, _getProject())));
		}

		IProjectBuilder builder = liferayProject.adapt(IProjectBuilder.class);

		if (builder == null) {
			throw new CoreException(
				PortletCore.createErrorStatus(NLS.bind(Msgs.couldNotCreateProjectBuilder, _getProject())));
		}

		monitor.worked(50);

		IStatus retval = builder.buildLang(langFile, monitor);

		if (retval == null) {
			retval = PortletCore.createErrorStatus(NLS.bind(Msgs.errorRunningBuildLang, _getProject()));
		}

		try {
			_getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (Exception e) {
			PortletCore.logError(e);
		}

		_getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

		try {
			_getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (Exception e) {
			PortletCore.logError(e);
		}

		if ((retval == null) || !retval.isOK()) {
			throw new CoreException(retval);
		}

		monitor.worked(90);
	}

	protected IFile langFile;

	private IProject _getProject() {
		return this.langFile.getProject();
	}

	private static class Msgs extends NLS {

		public static String buildingLanguages;
		public static String buildLanguages;
		public static String couldNotCreateLiferayProject;
		public static String couldNotCreateProjectBuilder;
		public static String errorRunningBuildLang;

		static {
			initializeMessages(BuildLanguageJob.class.getName(), Msgs.class);
		}

	}

}