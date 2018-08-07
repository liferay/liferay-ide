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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

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

	public InitBundleJob(String jobName, IProject project, String serverName, String bundleUrl) {
		super(jobName);

		_project = project;
		_bundleUrl = bundleUrl;

		this.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		this.addJobChangeListener(
			new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					LiferayWorkspaceUtil.addPortalRuntime(serverName);
				}

			});

		JobUtil.waitForLiferayProjectJob();
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