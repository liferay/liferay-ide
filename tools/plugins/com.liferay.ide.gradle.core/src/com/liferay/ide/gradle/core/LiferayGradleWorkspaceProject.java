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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.LiferayWorkspaceProject;
import com.liferay.ide.server.core.ILiferayServer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Andy Wu
 * @author Simon Jiang
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
	public void watch(Set<IProject> childProjects) {
		boolean hasRoot = childProjects.contains(getProject());

		final List<String> tasks = new ArrayList<>();

		if (hasRoot) {
			tasks.add("watch");
		}
		else {
			Stream<IProject> stream = childProjects.stream();

			stream.map(
				IProject::getLocation
			).map(
				location -> _convertToModuleTaskPath(location)
			).forEach(
				tasks::add
			);
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
					String[] args = new String[] {"--continuous", "--continue"};

					GradleUtil.runGradleTask(getProject(), tasks.toArray(new String[0]), args, monitor);
				}
				catch (Exception e) {
					return GradleCore.createErrorStatus(
						"Error running Gradle watch task for project " + getProject(), e);
				}

				return Status.OK_STATUS;
			}

		};

		job.setProperty(ILiferayServer.LIFERAY_SERVER_JOB, this);
		job.setSystem(true);

		_watchingProjects.clear();
		_watchingProjects.addAll(childProjects);

		job.schedule();
	}

	private String _convertToModuleTaskPath(IPath moduleLocation) {
		IProject project = getProject();

		IPath workspaceLocation = project.getLocation();

		String watchTask = ":watch";

		for (int i = moduleLocation.segmentCount() - 1; i >= 0; i--) {
			String segment = moduleLocation.segment(i);

			watchTask = ":" + segment + watchTask;

			IPath currentLocation = moduleLocation.removeLastSegments(moduleLocation.segmentCount() - i);

			if (workspaceLocation.equals(currentLocation)) {
				break;
			}
		}

		return watchTask;
	}

	@Override
	public Set<IProject> watching() {
		return Collections.unmodifiableSet(_watchingProjects);
	}
	private static final Set<IProject> _watchingProjects = new HashSet<>();
}