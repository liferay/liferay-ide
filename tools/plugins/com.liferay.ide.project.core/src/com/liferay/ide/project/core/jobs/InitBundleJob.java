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

package com.liferay.ide.project.core.jobs;

import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.JobUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author Charles Wu
 */
public class InitBundleJob extends Job {

	public InitBundleJob(IProject project, String serverName, String bundleUrl) {
		super("Initializing Liferay bundle");

		_project = project;
		_bundleUrl = bundleUrl;

		addJobChangeListener(
			new JobChangeAdapter() {

				@Override
				public void aboutToRun(IJobChangeEvent event) {
					JobUtil.waitForLiferayProjectJob();
				}

				@Override
				public void done(IJobChangeEvent event) {
					ServerUtil.addPortalRuntime(serverName);
				}

			});
	}

	@Override
	public boolean belongsTo(Object family) {
		return family.equals(LiferayCore.LIFERAY_JOB_FAMILY);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			IWorkspaceProjectBuilder workspaceProjectBuilder = LiferayWorkspaceUtil.getWorkspaceProjectBuilder(
				_project);

			workspaceProjectBuilder.initBundle(_project, _bundleUrl, monitor);
		}
		catch (CoreException ce) {
			return ProjectCore.createErrorStatus("Init Liferay Bundle failed", ce);
		}

		return Status.OK_STATUS;
	}

	private String _bundleUrl;
	private IProject _project;

}