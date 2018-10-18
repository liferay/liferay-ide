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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.LiferayWorkspaceProject;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Terry Jia
 */
public class LiferayGradleWorkspaceProject extends LiferayWorkspaceProject {

	public LiferayGradleWorkspaceProject(IProject project) {
		super(project);
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		if (IProjectBuilder.class.equals(adapterType) || IWorkspaceProjectBuilder.class.equals(adapterType)) {
			IProjectBuilder projectBuilder = new GradleProjectBuilder(getProject());

			return adapterType.cast(projectBuilder);
		}

		return super.adapt(adapterType);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		if (getProject() == null) {
			return null;
		}

		IPath projectLocation = getProject().getLocation();

		File gradleProperties = new File(projectLocation.toFile(), "gradle.properties");

		String retVal = null;

		if (FileUtil.exists(gradleProperties)) {
			Properties properties = PropertiesUtil.loadProperties(gradleProperties);

			retVal = properties.getProperty(key, defaultValue);
		}

		return retVal;
	}

	@Override
	public boolean isWatchable() {
		IProject project = getProject();

		IFile settingsGradleFile = project.getFile("settings.gradle");

		return GradleUtil.isWatchableProject(settingsGradleFile);
	}

	@Override
	public void watch(Set<IProject> childProjects) {
		boolean runOnRoot = false;
		Set<IProject> runOnProjects = childProjects;

		if (childProjects.contains(getProject())) {
			Stream<IProject> stream = getChildProjects().stream();

			long warCount = stream.map(
				project -> LiferayCore.create(IBundleProject.class, project)
			).filter(
				Objects::nonNull
			).filter(
				bundleProject -> "war".equals(bundleProject.getBundleShape())
			).count();

			if (warCount == 0) {
				runOnRoot = true;
				runOnProjects = Collections.singleton(getProject());
			}
			else {
				runOnProjects = getChildProjects();
			}
		}

		_executeTask(runOnRoot, runOnProjects);
	}

	@Override
	public Set<IProject> watching() {
		return Collections.unmodifiableSet(_watchingProjects);
	}

	private String _convertToModuleTaskPath(IPath moduleLocation, String taskName) {
		IProject project = getProject();

		IPath projectLocation = project.getLocation();

		String taskPath = ":" + taskName;

		for (int i = moduleLocation.segmentCount() - 1; i >= 0; i--) {
			String segment = moduleLocation.segment(i);

			taskPath = ":" + segment + taskPath;

			IPath currentLocation = moduleLocation.removeLastSegments(moduleLocation.segmentCount() - i);

			if (projectLocation.equals(currentLocation)) {
				break;
			}
		}

		return taskPath;
	}

	private void _executeTask(boolean runOnRoot, Set<IProject> childProjects) {
		final List<String> tasks = new ArrayList<>();

		if (runOnRoot) {
			tasks.add("watch");
		}
		else {
			for (IProject project : childProjects) {
				String taskName = "watch";

				IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

				if (!isWatchable() || ((bundleProject != null) && "war".equals(bundleProject.getBundleShape()))) {
					taskName = "deploy";
				}

				tasks.add(_convertToModuleTaskPath(project.getLocation(), taskName));
			}
		}

		String projectName = getProject().getName();

		String jobName = projectName + ":watch";

		IJobManager jobManager = Job.getJobManager();

		Job[] jobs = jobManager.find(jobName);

		if (ListUtil.isNotEmpty(jobs)) {
			Job job = jobs[0];

			job.cancel();

			try {
				job.join();
			}
			catch (InterruptedException ie) {
			}
		}

		Job job = new Job(jobName) {

			@Override
			public boolean belongsTo(Object family) {
				return jobName.equals(family);
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

					GradleUtil.runGradleTask(
						getProject(), tasks.toArray(new String[0]), args,
						LiferayGradleCore.WATCH_LAUNCH_CONFIGURATION_NAME, false, monitor);
				}
				catch (Exception e) {
					return LiferayGradleCore.createErrorStatus(
						"Error running watch task for project " + getProject(), e);
				}

				return Status.OK_STATUS;
			}

		};

		job.addJobChangeListener(
			new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					_watchingProjects.clear();
				}

			});

		job.setProperty(ILiferayServer.LIFERAY_SERVER_JOB, this);
		job.setSystem(true);

		_watchingProjects.clear();
		_watchingProjects.addAll(childProjects);

		if (ListUtil.isNotEmpty(childProjects)) {
			job.schedule();
		}
	}

	private static final Set<IProject> _watchingProjects = new HashSet<>();

}