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

package com.liferay.ide.gradle.core;

import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.ServerCore;

import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnector;

/**
 * @author Simon Jiang
 */
public class WatchJob extends Job {

	public WatchJob(IProject project, List<String> tasks, String jobNameSuffix) {
		super(project.getName() + ":" + LiferayGradleCore.LIFERAY_WATCH + ":" + jobNameSuffix);

		_project = project;
		_tasks = tasks;
		_cancelToken = GradleConnector.newCancellationTokenSource();
	}

	@Override
	public boolean belongsTo(Object family) {
		return getName().equals(family);
	}

	@Override
	public void canceling() {
		_cancelToken.cancel();
	}

	public CancellationTokenSource getCancelToker() {
		return _cancelToken;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			Stream.of(
				ServerCore.getServers()
			).map(
				server -> (PortalServerBehavior)server.loadAdapter(
					PortalServerBehavior.class, new NullProgressMonitor())
			).filter(
				serverBehavior -> serverBehavior != null
			).forEach(
				serverBehavior -> serverBehavior.refreshSourceLookup()
			);

			String[] args = {"--continuous", "--continue"};

			GradleUtil.runGradleTask(_project, _tasks.toArray(new String[0]), args, _cancelToken, monitor);
		}
		catch (Exception e) {
			return LiferayGradleCore.createErrorStatus("Error running watch task for project " + _project, e);
		}

		return Status.OK_STATUS;
	}

	private CancellationTokenSource _cancelToken;
	private IProject _project;
	private List<String> _tasks;

}