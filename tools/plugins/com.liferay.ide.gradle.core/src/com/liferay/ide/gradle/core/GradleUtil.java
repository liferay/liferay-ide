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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import org.eclipse.buildship.core.BuildConfiguration;
import org.eclipse.buildship.core.BuildConfiguration.BuildConfigurationBuilder;
import org.eclipse.buildship.core.GradleBuild;
import org.eclipse.buildship.core.GradleCore;
import org.eclipse.buildship.core.GradleDistribution;
import org.eclipse.buildship.core.GradleWorkspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.GradleProject;

import org.osgi.framework.Version;

/**
 * @author Andy Wu
 * @author Lovett Li
 * @author Charles Wu
 * @author Simon Jiang
 */
public class GradleUtil {

	public static GradleProject getGradleProject(IProject project) {
		if (project == null) {
			return null;
		}

		GradleProject workspaceGradleModel = getWorkspaceGradleProject(project);

		if (workspaceGradleModel == null) {
			return null;
		}

		return getNestedGradleModel(workspaceGradleModel, project.getName());
	}

	public static GradleProject getNestedGradleModel(GradleProject gradleProject, String projectName) {
		if (gradleProject == null) {
			return null;
		}

		GradleProject nestedGradleProject = null;

		try {
			String gradleProjectName = gradleProject.getName();

			if (gradleProjectName.equals(projectName)) {
				return gradleProject;
			}

			DomainObjectSet<? extends GradleProject> childGradleProjects = gradleProject.getChildren();

			if (!childGradleProjects.isEmpty()) {
				for (GradleProject childGradleProject : childGradleProjects) {
					String childProjectName = childGradleProject.getName();

					if (childProjectName.equals(projectName)) {
						return childGradleProject;
					}

					nestedGradleProject = getNestedGradleModel(childGradleProject, projectName);

					if (nestedGradleProject != null) {
						return nestedGradleProject;
					}
				}
			}
		}
		catch (Exception e) {
			LiferayGradleCore.logError("Fetch gradle model error ", e);
		}

		return nestedGradleProject;
	}

	public static GradleProject getWorkspaceGradleProject(IProject project) {
		try {
			GradleWorkspace gradleWorkspace = GradleCore.getWorkspace();

			Optional<GradleBuild> optionalGradleBuild = gradleWorkspace.getBuild(project);

			GradleBuild gradleBuild = optionalGradleBuild.get();

			return gradleBuild.withConnection(
				connection -> {
					ModelBuilder<GradleProject> model = connection.model(GradleProject.class);

					return model.get();
				},
				new NullProgressMonitor());
		}
		catch (Exception e) {
		}

		return null;
	}

	public static boolean isBuildFile(IFile buildFile) {
		if (FileUtil.exists(buildFile) && Objects.equals("build.gradle", buildFile.getName()) &&
			(buildFile.getParent() instanceof IProject)) {

			return true;
		}

		return false;
	}

	public static boolean isGradleProject(Object resource) {
		IProject project = null;

		if (resource instanceof IFile) {
			IFile file = (IFile)resource;

			project = file.getProject();
		}
		else if (resource instanceof IProject) {
			project = (IProject)resource;
		}

		if (project != null) {
			try {
				return project.hasNature("org.eclipse.buildship.core.gradleprojectnature");
			}
			catch (CoreException ce) {
			}
		}

		return false;
	}

	public static boolean isWatchableProject(IFile buildFile) {
		if (FileUtil.notExists(buildFile)) {
			return false;
		}

		boolean watchable = false;

		try {
			GradleBuildScript gradleBuildScript = new GradleBuildScript(FileUtil.getFile(buildFile));

			List<GradleDependency> dependencies = gradleBuildScript.getBuildScriptDependencies();

			for (GradleDependency dependency : dependencies) {
				String group = dependency.getGroup();
				String name = dependency.getName();
				Version version = new Version("0");
				String dependencyVersion = dependency.getVersion();

				try {
					if ((dependencyVersion != null) && !dependencyVersion.equals("")) {
						version = Version.parseVersion(dependencyVersion);
					}

					if (group.equals("com.liferay") && name.equals("com.liferay.gradle.plugins") &&
						(CoreUtil.compareVersions(version, new Version("3.11.0")) >= 0)) {

						watchable = true;

						break;
					}

					if (group.equals("com.liferay") && name.equals("com.liferay.gradle.plugins.workspace") &&
						(CoreUtil.compareVersions(version, new Version("1.9.2")) >= 0)) {

						watchable = true;

						break;
					}
				}
				catch (IllegalArgumentException iae) {
				}
			}
		}
		catch (IOException ioe) {
		}
		catch (MultipleCompilationErrorsException mcee) {
		}

		return watchable;
	}

	public static void refreshProject(IProject project) {
		GradleWorkspace workspace = GradleCore.getWorkspace();

		Optional<GradleBuild> buildOpt = workspace.getBuild(project);

		Job synchronizeJob = new Job("Liferay refresh gradle project job") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (buildOpt.isPresent()) {
					GradleBuild gradleBuild = buildOpt.get();

					gradleBuild.synchronize(monitor);
				}

				return Status.OK_STATUS;
			}

		};

		synchronizeJob.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		synchronizeJob.schedule();
	}

	public static String runGradleTask(IProject project, String task, boolean redirectOutput, IProgressMonitor monitor)
		throws CoreException {

		CancellationTokenSource cancellationTokenSource = GradleConnector.newCancellationTokenSource();

		return runGradleTask(
			project, new String[] {task}, new String[0], cancellationTokenSource, redirectOutput, monitor);
	}

	public static void runGradleTask(IProject project, String[] tasks, IProgressMonitor monitor) throws CoreException {
		CancellationTokenSource cancellationTokenSource = GradleConnector.newCancellationTokenSource();

		runGradleTask(project, tasks, new String[0], cancellationTokenSource, false, monitor);
	}

	public static String runGradleTask(
			IProject project, String[] tasks, String[] arguments, boolean redirectOutput, IProgressMonitor monitor)
		throws CoreException {

		CancellationTokenSource cancellationTokenSource = GradleConnector.newCancellationTokenSource();

		return runGradleTask(project, tasks, arguments, cancellationTokenSource, redirectOutput, monitor);
	}

	public static String runGradleTask(
			IProject project, String[] tasks, String[] arguments, CancellationTokenSource cancellationTokenSource,
			boolean redirectOutput, IProgressMonitor monitor)
		throws CoreException {

		if ((project == null) || (project.getLocation() == null)) {
			return "";
		}

		GradleWorkspace workspace = GradleCore.getWorkspace();

		Optional<GradleBuild> gradleBuildOpt = workspace.getBuild(project);

		GradleBuild gradleBuild = gradleBuildOpt.get();

		try {
			if (redirectOutput) {
				OutputStream outputStream = new ByteArrayOutputStream();

				gradleBuild.withConnection(
					connection -> {
						connection.newBuild(
						).addArguments(
							arguments
						).forTasks(
							tasks
						).withCancellationToken(
							cancellationTokenSource.token()
						).setStandardOutput(
							outputStream
						).run();

						return null;
					},
					monitor);

				return outputStream.toString();
			}

			gradleBuild.withConnection(
				connection -> {
					connection.newBuild(
					).addArguments(
						arguments
					).forTasks(
						tasks
					).withCancellationToken(
						cancellationTokenSource.token()
					).run();

					return null;
				},
				monitor);
		}
		catch (Exception e) {
			LiferayGradleCore.logError(e);
		}

		return null;
	}

	public static IStatus synchronizeProject(IPath dir, IProgressMonitor monitor) {
		if (FileUtil.notExists(dir)) {
			return LiferayGradleCore.createErrorStatus("Unable to find gradle project at " + dir);
		}

		BuildConfigurationBuilder gradleBuilder = BuildConfiguration.forRootProjectDirectory(dir.toFile());

		gradleBuilder.overrideWorkspaceConfiguration(true);
		gradleBuilder.autoSync(true);
		gradleBuilder.buildScansEnabled(false);
		gradleBuilder.gradleDistribution(GradleDistribution.fromBuild());
		gradleBuilder.showConsoleView(true);
		gradleBuilder.showExecutionsView(true);

		BuildConfiguration configuration = gradleBuilder.build();

		GradleWorkspace workspace = GradleCore.getWorkspace();

		GradleBuild gradleBuild = workspace.createBuild(configuration);

		Job synchronizeJob = new Job("Liferay sychronized gradle project job") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				gradleBuild.synchronize(monitor);

				return Status.OK_STATUS;
			}

		};

		synchronizeJob.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		synchronizeJob.setProgressGroup(monitor, IProgressMonitor.UNKNOWN);

		synchronizeJob.schedule();

		return Status.OK_STATUS;
	}

}