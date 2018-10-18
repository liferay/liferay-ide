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

import com.google.common.base.Optional;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import org.eclipse.buildship.core.BuildConfiguration;
import org.eclipse.buildship.core.BuildConfiguration.BuildConfigurationBuilder;
import org.eclipse.buildship.core.GradleBuild;
import org.eclipse.buildship.core.GradleCore;
import org.eclipse.buildship.core.GradleDistribution;
import org.eclipse.buildship.core.GradleWorkspace;
import org.eclipse.buildship.core.internal.CorePlugin;
import org.eclipse.buildship.core.internal.configuration.ConfigurationManager;
import org.eclipse.buildship.core.internal.configuration.GradleProjectNature;
import org.eclipse.buildship.core.internal.launch.GradleLaunchConfigurationManager;
import org.eclipse.buildship.core.internal.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.internal.launch.GradleRunConfigurationDelegate;
import org.eclipse.buildship.core.internal.util.variable.ExpressionUtils;
import org.eclipse.buildship.core.internal.workspace.NewProjectHandler;
import org.eclipse.buildship.core.internal.workspace.SynchronizationJob;
import org.eclipse.buildship.core.internal.workspace.WorkspaceOperations;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import org.osgi.framework.Version;

/**
 * @author Andy Wu
 * @author Lovett Li
 * @author Charles Wu
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class GradleUtil {

	public static boolean isBuildFile(IFile buildFile) {
		if (FileUtil.exists(buildFile) && "build.gradle".equals(buildFile.getName()) &&
			buildFile.getParent() instanceof IProject) {

			return true;
		}

		return false;
	}

	public static boolean isGradleProject(Object resource) throws CoreException {
		IProject project = null;

		if (resource instanceof IFile) {
			project = ((IFile)resource).getProject();
		}
		else if (resource instanceof IProject) {
			project = (IProject)resource;
		}

		return GradleProjectNature.isPresentOn(project);
	}

	public static boolean isWatchableProject(IFile buildFile) {
		if (FileUtil.notExists(buildFile)) {
			return false;
		}

		boolean watchable = false;

		try {
			GradleDependencyUpdater updater = new GradleDependencyUpdater(buildFile);

			List<GradleDependency> dependencies = updater.getAllBuildDependencies();

			for (GradleDependency dependency : dependencies) {
				String group = dependency.getGroup();
				String name = dependency.getName();
				Version version = new Version("0");
				String dependencyVersion = dependency.getVersion();

				try {
					if ((dependencyVersion != null) && !dependencyVersion.equals("")) {
						version = new Version(dependencyVersion);
					}

					if ("com.liferay".equals(group) && "com.liferay.gradle.plugins".equals(name) &&
						(CoreUtil.compareVersions(version, new Version("3.11.0")) >= 0)) {

						watchable = true;

						break;
					}

					if ("com.liferay".equals(group) && "com.liferay.gradle.plugins.workspace".equals(name) &&
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

		java.util.Optional<GradleBuild> buildOpt = workspace.getBuild(project);

		GradleBuild gradleBuild = buildOpt.get();

		SynchronizationJob job = new SynchronizationJob(NewProjectHandler.IMPORT_AND_MERGE, gradleBuild);

		job.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		job.schedule();
	}

	public static void runGradleTask(IProject project, String task, IProgressMonitor monitor) throws CoreException {
		runGradleTask(project, new String[] {task}, new String[0], null, true, monitor);
	}

	public static void runGradleTask(
			IProject project, String[] tasks, String[] arguments, String launchName, boolean saveConfig,
			IProgressMonitor monitor)
		throws CoreException {

		GradleRunConfigurationAttributes runAttributes = _getRunConfigurationAttributes(
			project, Arrays.asList(tasks), Arrays.asList(arguments));

		final ILaunchConfigurationWorkingCopy launchConfigurationWC;

		if (launchName == null) {
			GradleLaunchConfigurationManager launchConfigManager = CorePlugin.gradleLaunchConfigurationManager();

			ILaunchConfiguration launchConfiguration = launchConfigManager.getOrCreateRunConfiguration(runAttributes);

			launchConfigurationWC = launchConfiguration.getWorkingCopy();
		}
		else {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();

			ILaunchManager launchManager = debugPlugin.getLaunchManager();

			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(
				GradleRunConfigurationDelegate.ID);

			launchConfigurationWC = launchConfigurationType.newInstance(null, launchName);

			runAttributes.apply(launchConfigurationWC);

			if (saveConfig) {
				launchConfigurationWC.doSave();
			}
		}

		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", Boolean.TRUE);
		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", Boolean.TRUE);
		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_PRIVATE", Boolean.TRUE);
		launchConfigurationWC.launch(ILaunchManager.RUN_MODE, monitor);
	}

	public static IStatus sychronizeProject(IPath dir, IProgressMonitor monitor) {
		if (FileUtil.notExists(dir)) {
			return LiferayGradleCore.createErrorStatus("Unable to find gradle project at " + dir);
		}

		BuildConfigurationBuilder gradleBuilder = BuildConfiguration.forRootProjectDirectory(dir.toFile());

		gradleBuilder.overrideWorkspaceConfiguration(true);
		gradleBuilder.autoSync(true);
		gradleBuilder.buildScansEnabled(true);
		gradleBuilder.gradleDistribution(GradleDistribution.fromBuild());

		BuildConfiguration configuration = gradleBuilder.build();

		GradleWorkspace workspace = GradleCore.getWorkspace();

		GradleBuild gradleBuild = workspace.createBuild(configuration);

		SynchronizationJob synchronizeJob = new SynchronizationJob(NewProjectHandler.IMPORT_AND_MERGE, gradleBuild);

		synchronizeJob.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		synchronizeJob.setProgressGroup(monitor, IProgressMonitor.UNKNOWN);

		synchronizeJob.schedule();

		return Status.OK_STATUS;
	}

	private static GradleRunConfigurationAttributes _getRunConfigurationAttributes(
		IProject project, List<String> tasks, List<String> arguments) {

		IPath projecLocation = project.getLocation();

		File rootDir = projecLocation.toFile();

		ConfigurationManager configurationManager = CorePlugin.configurationManager();

		org.eclipse.buildship.core.internal.configuration.BuildConfiguration buildConfig =
			configurationManager.loadBuildConfiguration(rootDir);

		String projectDirectoryExpression = null;

		WorkspaceOperations workspaceOperations = CorePlugin.workspaceOperations();

		Optional<IProject> gradleProject = workspaceOperations.findProjectByLocation(rootDir);

		if (gradleProject.isPresent()) {
			projectDirectoryExpression = ExpressionUtils.encodeWorkspaceLocation(gradleProject.get());
		}
		else {
			projectDirectoryExpression = rootDir.getAbsolutePath();
		}

		File gradleHome = buildConfig.getGradleUserHome();

		String gradleUserHome = gradleHome == null ? "" : gradleHome.getAbsolutePath();

		GradleDistribution gradleDistribution = buildConfig.getGradleDistribution();

		String serializeString = gradleDistribution.toString();

		return new GradleRunConfigurationAttributes(
			tasks, projectDirectoryExpression, serializeString, gradleUserHome, null, Collections.<String>emptyList(),
			arguments, true, true, buildConfig.isOverrideWorkspaceSettings(), buildConfig.isOfflineMode(),
			buildConfig.isBuildScansEnabled());
	}

}