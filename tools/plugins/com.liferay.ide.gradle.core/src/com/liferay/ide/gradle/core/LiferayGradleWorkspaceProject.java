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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.Event;
import com.liferay.ide.core.EventListener;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.core.workspace.ProjectChangedEvent;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.LiferayWorkspaceProject;
import com.liferay.ide.server.core.ILiferayServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.osgi.framework.Version;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Terry Jia
 * @author Ethan Sun
 */
public class LiferayGradleWorkspaceProject extends LiferayWorkspaceProject implements EventListener {

	public LiferayGradleWorkspaceProject(IProject project) {
		super(project);

		IPath projectPath = project.getFullPath();

		_importantResources = new IPath[] {
			projectPath.append("gradle.properties"), projectPath.append("build.gradle"),
			projectPath.append("settings.gradle")
		};

		_initializeGradleWorkspaceProperties(project);
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
	public List<Artifact> getTargetPlatformArtifacts() {
		if ((getTargetPlatformVersion() != null) && _targetPlatformArtifacts.isEmpty()) {
			String output = "";

			GradleProject gradleModel = GradleUtil.getGradleProject(getProject());

			DomainObjectSet<? extends GradleTask> tasksSet = gradleModel.getTasks();

			List<? extends GradleTask> tasksList = tasksSet.getAll();

			String tasks = tasksList.toString();

			if (tasks.contains("LaunchableGradleProjectTask{path=':dependencyManagement',public=true}")) {
				try {
					output = GradleUtil.runGradleTask(
						LiferayWorkspaceUtil.getWorkspaceProject(), "dependencyManagement", true,
						new NullProgressMonitor());
				}
				catch (CoreException ce) {
				}

				IProject project = getProject();

				IFile settingsGradleFile = project.getFile("settings.gradle");

				GradleDependencyUpdater gradleDependencyUpdater = null;

				try {
					gradleDependencyUpdater = new GradleDependencyUpdater(settingsGradleFile);
				}
				catch (IOException ioe) {
				}

				List<Artifact> dependencies = gradleDependencyUpdater.getDependencies(true, "classpath");

				final String workspacePluginVersion = dependencies.stream(
				).filter(
					artifact -> "com.liferay".equals(artifact.getGroupId())
				).filter(
					artifact -> "com.liferay.gradle.plugins.workspace".equals(artifact.getArtifactId())
				).filter(
					artifact -> CoreUtil.isNotNullOrEmpty(artifact.getVersion())
				).map(
					artifact -> artifact.getVersion()
				).findFirst(
				).orElseGet(
					() -> "2.2.4"
				);

				String taskOutputInfo;

				if (CoreUtil.compareVersions(new Version(workspacePluginVersion), new Version("2.2.4")) < 0) {
					taskOutputInfo = "compileOnly - Dependency management for the compileOnly configuration";
				}
				else {
					taskOutputInfo = "> Task :dependencyManagement";
				}

				List<String> list = new ArrayList<>();

				if (CoreUtil.isNotNullOrEmpty(output) && !output.equals("")) {
					BufferedReader bufferedReader = new BufferedReader(new StringReader(output));

					String line;

					try {
						boolean start = false;

						while ((line = bufferedReader.readLine()) != null) {
							if (taskOutputInfo.equals(line)) {
								start = true;

								continue;
							}

							if (start) {
								if (StringUtil.equals(line.trim(), "")) {
									break;
								}

								list.add(line.trim());
							}
						}
					}
					catch (IOException ioe) {
					}
				}
				else {
					LiferayGradleCore.log(
						LiferayGradleCore.createWarningStatus(
							new String("Please check liferay target platform dependencies.")));
				}

				_targetPlatformArtifacts = list.stream(
				).map(
					s -> {
						String groupId;
						String artifactId;
						String version;

						if (CoreUtil.compareVersions(new Version(workspacePluginVersion), new Version("2.2.4")) < 0) {
							int i1 = s.indexOf(":");
							int i2 = s.indexOf(" ");

							groupId = s.substring(0, i1);
							artifactId = s.substring(i1 + 1, i2);
							version = s.substring(i2 + 1);
						}
						else {
							String[] artifactArray = s.split(":");

							groupId = artifactArray[0];
							artifactId = artifactArray[1];
							version = artifactArray[2];
						}

						Artifact artifact = new Artifact(groupId, artifactId, version, "compileOnly", null);

						return artifact;
					}
				).collect(
					Collectors.toList()
				);
			}
		}

		return _targetPlatformArtifacts;
	}

	@Override
	public String getTargetPlatformVersion() {
		IProject project = getProject();

		IPath location = project.getLocation();

		return LiferayWorkspaceUtil.getGradleProperty(
			location.toString(), WorkspaceConstants.TARGET_PLATFORM_VERSION_PROPERTY, null);
	}

	@Override
	public boolean isStale() {
		return _stale;
	}

	@Override
	public boolean isWatchable() {
		IProject project = getProject();

		IFile settingsGradleFile = project.getFile("settings.gradle");

		return GradleUtil.isWatchableProject(settingsGradleFile);
	}

	@Override
	public void onEvent(Event event) {
		Optional.of(
			event
		).filter(
			e -> !isStale()
		).filter(
			ProjectChangedEvent.class::isInstance
		).map(
			ProjectChangedEvent.class::cast
		).filter(
			projectChangedEvent -> hasResourcesAffected(projectChangedEvent, getProject(), _importantResources)
		).ifPresent(
			e -> _stale = true
		);
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

	private void _initializeGradleWorkspaceProperties(IProject project) {
		if (project.exists()) {
			IPath projectLocation = project.getLocation();

			IPath gradleProperties = projectLocation.append("gradle.properties");

			properties.putAll(PropertiesUtil.loadProperties(gradleProperties));
		}
	}

	private static final Set<IProject> _watchingProjects = new HashSet<>();

	private IPath[] _importantResources;
	private volatile boolean _stale = false;
	private List<Artifact> _targetPlatformArtifacts = Collections.emptyList();

}