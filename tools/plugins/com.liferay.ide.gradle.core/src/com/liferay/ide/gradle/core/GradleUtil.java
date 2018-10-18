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

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.BuildConfiguration;
import org.eclipse.buildship.core.configuration.ConfigurationManager;
import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.buildship.core.configuration.WorkspaceConfiguration;
import org.eclipse.buildship.core.launch.GradleLaunchConfigurationManager;
import org.eclipse.buildship.core.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.launch.GradleRunConfigurationDelegate;
import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.binding.Validator;
import org.eclipse.buildship.core.util.binding.Validators;
import org.eclipse.buildship.core.util.gradle.GradleDistribution;
import org.eclipse.buildship.core.util.gradle.GradleDistributionInfo;
import org.eclipse.buildship.core.util.variable.ExpressionUtils;
import org.eclipse.buildship.core.workspace.GradleBuild;
import org.eclipse.buildship.core.workspace.GradleWorkspaceManager;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.buildship.core.workspace.SynchronizationJob;
import org.eclipse.buildship.core.workspace.WorkspaceOperations;
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
		GradleWorkspaceManager gradleWorkspaceManager = CorePlugin.gradleWorkspaceManager();

		Optional<GradleBuild> optional = gradleWorkspaceManager.getGradleBuild(project);

		IPath path = project.getFullPath();

		GradleBuild build = null;

		if (!optional.isPresent()) {
			BuildConfiguration buildConfig = _createBuildConfiguration(path.toFile());

			build = gradleWorkspaceManager.getGradleBuild(buildConfig);
		}
		else {
			build = optional.get();
		}

		SynchronizationJob job = new SynchronizationJob(NewProjectHandler.IMPORT_AND_MERGE, build);

		job.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		job.schedule();
	}

	public static void runGradleTask(IProject project, String task, IProgressMonitor monitor) throws CoreException {
		runGradleTask(project, new String[] {task}, monitor);
	}

	public static void runGradleTask(IProject project, String[] tasks, IProgressMonitor monitor) throws CoreException {
		runGradleTask(project, tasks, new String[0], monitor);
	}

	public static void runGradleTask(IProject project, String[] tasks, String[] arguments, IProgressMonitor monitor)
		throws CoreException {

		GradleLaunchConfigurationManager launchConfigManager = CorePlugin.gradleLaunchConfigurationManager();

		ILaunchConfiguration launchConfiguration = launchConfigManager.getOrCreateRunConfiguration(
			_getRunConfigurationAttributes(project, Arrays.asList(tasks), Arrays.asList(arguments)));

		final ILaunchConfigurationWorkingCopy launchConfigurationWC = launchConfiguration.getWorkingCopy();

		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", Boolean.TRUE);
		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", Boolean.TRUE);
		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_PRIVATE", Boolean.TRUE);

		launchConfigurationWC.doSave();

		launchConfigurationWC.launch(ILaunchManager.RUN_MODE, monitor);
	}

	public static void runGradleTask(
			IProject project, String[] tasks, String[] arguments, String launchName, IProgressMonitor monitor)
		throws CoreException {

		GradleRunConfigurationAttributes runAttributes = _getRunConfigurationAttributes(
			project, Arrays.asList(tasks), Arrays.asList(arguments));

		DebugPlugin plugin = DebugPlugin.getDefault();

		ILaunchManager launchManager = plugin.getLaunchManager();

		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(
			GradleRunConfigurationDelegate.ID);

		ILaunchConfigurationWorkingCopy launchConfiguration = launchConfigurationType.newInstance(null, launchName);

		launchConfiguration.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", Boolean.TRUE);
		launchConfiguration.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", Boolean.TRUE);
		launchConfiguration.setAttribute("org.eclipse.debug.ui.ATTR_PRIVATE", Boolean.TRUE);

		runAttributes.apply(launchConfiguration);

		//run without persist configuration

		launchConfiguration.launch(ILaunchManager.RUN_MODE, monitor);
	}

	public static IStatus sychronizeProject(IPath dir, IProgressMonitor monitor) {
		if (FileUtil.notExists(dir)) {
			return GradleCore.createErrorStatus("Unable to find gradle project at " + dir);
		}

		BuildConfiguration buildConfig = _createBuildConfiguration(dir.toFile());

		GradleWorkspaceManager gradleWorkspaceManager = CorePlugin.gradleWorkspaceManager();

		GradleBuild build = gradleWorkspaceManager.getGradleBuild(buildConfig);

		SynchronizationJob synchronizeJob = new SynchronizationJob(NewProjectHandler.IMPORT_AND_MERGE, build);

		synchronizeJob.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

		synchronizeJob.setProgressGroup(monitor, IProgressMonitor.UNKNOWN);

		synchronizeJob.schedule();

		return Status.OK_STATUS;
	}

	private static BuildConfiguration _createBuildConfiguration(File file) {
		Validator<File> projectDirValidator = Validators.and(
			Validators.requiredDirectoryValidator("Project root directory"),
			Validators.nonWorkspaceFolderValidator("Project root directory"));

		Validator<GradleDistributionInfo> gradleDistributionValidator = GradleDistributionInfo.validator();

		Validator<Boolean> applyWorkingSetsValidator = Validators.nullValidator();
		Validator<List<String>> workingSetsValidator = Validators.nullValidator();
		Validator<File> gradleUserHomeValidator = Validators.optionalDirectoryValidator("Gradle user home");

		ProjectImportConfiguration configuration = new ProjectImportConfiguration(
			projectDirValidator, gradleDistributionValidator, gradleUserHomeValidator, applyWorkingSetsValidator,
			workingSetsValidator);

		// read configuration from gradle preference

		ConfigurationManager configurationManager = CorePlugin.configurationManager();

		WorkspaceConfiguration gradleConfig = configurationManager.loadWorkspaceConfiguration();

		GradleDistribution gradleDistribution = gradleConfig.getGradleDistribution();

		configuration.setProjectDir(file);
		configuration.setOverwriteWorkspaceSettings(false);
		configuration.setDistributionInfo(gradleDistribution.getDistributionInfo());
		configuration.setGradleUserHome(gradleConfig.getGradleUserHome());
		configuration.setApplyWorkingSets(false);
		configuration.setBuildScansEnabled(gradleConfig.isBuildScansEnabled());
		configuration.setOfflineMode(gradleConfig.isOffline());
		configuration.setAutoSync(true);

		BuildConfiguration buildConfig = configuration.toBuildConfig();

		return buildConfig;
	}

	private static GradleRunConfigurationAttributes _getRunConfigurationAttributes(
		IProject project, List<String> tasks, List<String> arguments) {

		IPath projecLocation = project.getLocation();

		File rootDir = projecLocation.toFile();

		ConfigurationManager configurationManager = CorePlugin.configurationManager();

		BuildConfiguration buildConfig = configurationManager.loadBuildConfiguration(rootDir);

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

		String serializeString = gradleDistribution.serializeToString();

		return new GradleRunConfigurationAttributes(
			tasks, projectDirectoryExpression, serializeString, gradleUserHome, null, Collections.<String>emptyList(),
			arguments, true, true, buildConfig.isOverrideWorkspaceSettings(), buildConfig.isOfflineMode(),
			buildConfig.isBuildScansEnabled());
	}

}