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
import com.liferay.ide.core.ILiferayProjectCacheEntry;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.LiferayWorkspaceProject;
import com.liferay.ide.server.core.ILiferayServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Terry Jia
 */
public class LiferayGradleWorkspaceProject extends LiferayWorkspaceProject implements IResourceChangeListener {

	public LiferayGradleWorkspaceProject(IProject project) {
		super(project);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

		_initializeGradleWorkspaceProperties(project);
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		if (IProjectBuilder.class.equals(adapterType) || IWorkspaceProjectBuilder.class.equals(adapterType)) {
			IProjectBuilder projectBuilder = new GradleProjectBuilder(getProject());

			return adapterType.cast(projectBuilder);
		}

		if (ILiferayProjectCacheEntry.class.equals(adapterType)) {
			return adapterType.cast(
				new ILiferayProjectCacheEntry() {

					@Override
					public boolean isStale() {
						return _stale;
					}

				});
		}

		return super.adapt(adapterType);
	}

	@Override
	public Set<IProject> getChildProjects() {
		Set<IProject> childProjects = super.getChildProjects();

		Stream<IProject> childProjectsStream = childProjects.stream();

		return childProjectsStream.filter(
			childProject -> {
				try {
					return GradleUtil.isGradleProject(childProject);
				}
				catch (Exception e) {
					return false;
				}
			}
		).collect(
			Collectors.toSet()
		);
	}

	@Override
	public String getLiferayHome() {
		return getProperty(WorkspaceConstants.HOME_DIR_PROPERTY, WorkspaceConstants.DEFAULT_HOME_DIR);
	}

	@Override
	public boolean isWatchable() {
		IProject project = getProject();

		IFile settingsGradleFile = project.getFile("settings.gradle");

		return GradleUtil.isWatchableProject(settingsGradleFile);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta resourceDelta = event.getDelta();

		if (resourceDelta != null) {
			try {
				_visitResourceDelta(resourceDelta);
			}
			catch (CoreException ce) {
				_stale = true;

				IWorkspace workspace = ResourcesPlugin.getWorkspace();

				workspace.removeResourceChangeListener(this);
			}
		}
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

	private Set<IPath> _collectAffectedResourcePaths(IResourceDelta[] resourceDeltas) {
		Set<IPath> result = new HashSet<>();

		_collectAffectedResourcePaths(result, resourceDeltas);

		return result;
	}

	private void _collectAffectedResourcePaths(Set<IPath> paths, IResourceDelta[] resourceDeltas) {
		for (IResourceDelta resourceDelta : resourceDeltas) {
			IResource resource = resourceDelta.getResource();

			paths.add(resource.getProjectRelativePath());

			_collectAffectedResourcePaths(paths, resourceDelta.getAffectedChildren());
		}
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

	private boolean _doVisitDelta(IResourceDelta resourceDelta) {
		IResource resource = resourceDelta.getResource();

		if (resource instanceof IProject) {
			IProject project = (IProject)resource;

			if (project.equals(getProject())) {
				if (_importantResourcesAffected(resourceDelta)) {
					_stale = true;

					IWorkspace workspace = ResourcesPlugin.getWorkspace();

					workspace.removeResourceChangeListener(this);
				}
			}

			return false;
		}
		else {
			return resource instanceof IWorkspaceRoot;
		}
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

		IJobManager jobManager = Job.getJobManager();

		Job[] jobs = jobManager.find(
			getProject().getName() + ":" + LiferayGradleCore.LIFERAY_WATCH + ":" +
				LiferayGradleCore.LIFERAY_WORKSPACE_WATCH_JOB_FAMILY);

		if (ListUtil.isNotEmpty(jobs)) {
			Job job = jobs[0];

			job.cancel();

			try {
				job.join();
			}
			catch (InterruptedException ie) {
			}
		}

		Job job = new WatchJob(getProject(), tasks, LiferayGradleCore.LIFERAY_WORKSPACE_WATCH_JOB_FAMILY);

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

	private boolean _importantResourcesAffected(IResourceDelta resourceDelta) {
		Set<IPath> affectedResourcePaths = _collectAffectedResourcePaths(resourceDelta.getAffectedChildren());

		IProject project = getProject();

		IFile buildGradle = project.getFile("build.gradle");

		IPath buildGradlePath = buildGradle.getProjectRelativePath();

		if (affectedResourcePaths.contains(buildGradlePath)) {
			return true;
		}

		IFile settingsGradle = project.getFile("settings.gradle");

		IPath settingsGradletPath = settingsGradle.getProjectRelativePath();

		if (affectedResourcePaths.contains(settingsGradletPath)) {
			return true;
		}

		return false;
	}

	private void _visitResourceDelta(IResourceDelta resourceDelta) throws CoreException {
		resourceDelta.accept(
			new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					try {
						return _doVisitDelta(delta);
					}
					catch (Exception e) {
						_stale = true;

						IWorkspace workspace = ResourcesPlugin.getWorkspace();

						workspace.removeResourceChangeListener(LiferayGradleWorkspaceProject.this);

						throw new CoreException(new Status(IStatus.WARNING, GradleCore.PLUGIN_ID, e.getMessage(), e));
					}
				}

			});
	}

	private void _initializeGradleWorkspaceProperties(IProject project) {
		if (project.exists()) {
			IPath projectLocation = project.getLocation();

			IPath gradleProperties = projectLocation.append("gradle.properties");

			properties.putAll(PropertiesUtil.loadProperties(gradleProperties));
		}
	}

	private static final Set<IProject> _watchingProjects = new HashSet<>();

	private boolean _stale = false;

}