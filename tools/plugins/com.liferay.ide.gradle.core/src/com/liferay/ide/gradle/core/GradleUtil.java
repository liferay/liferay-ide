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

import com.gradleware.tooling.toolingutils.binding.Validator;

import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.BuildConfiguration;
import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.buildship.core.configuration.WorkspaceConfiguration;
import org.eclipse.buildship.core.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.binding.Validators;
import org.eclipse.buildship.core.util.gradle.GradleDistributionSerializer;
import org.eclipse.buildship.core.util.gradle.GradleDistributionValidator;
import org.eclipse.buildship.core.util.gradle.GradleDistributionWrapper;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.util.variable.ExpressionUtils;
import org.eclipse.buildship.core.workspace.GradleBuild;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author Andy Wu
 * @author Lovett Li
 */
@SuppressWarnings("restriction")
public class GradleUtil {

	public static IStatus importGradleProject(File dir, IProgressMonitor monitor) throws CoreException {
		Validator<File> projectDirValidator = Validators.and(
			Validators.requiredDirectoryValidator("Project root directory"),
			Validators.nonWorkspaceFolderValidator("Project root directory"));

		Validator<GradleDistributionWrapper> gradleDistributionValidator =
			GradleDistributionValidator.gradleDistributionValidator();

		Validator<Boolean> applyWorkingSetsValidator = Validators.nullValidator();
		Validator<List<String>> workingSetsValidator = Validators.nullValidator();
		Validator<File> gradleUserHomeValidator = Validators.optionalDirectoryValidator("Gradle user home");

		ProjectImportConfiguration configuration = new ProjectImportConfiguration(
			projectDirValidator, gradleDistributionValidator, gradleUserHomeValidator, applyWorkingSetsValidator,
			workingSetsValidator);

		// read configuration from gradle preference

		WorkspaceConfiguration gradleConfig = CorePlugin.configurationManager().loadWorkspaceConfiguration();

		configuration.setProjectDir(dir);
		configuration.setOverwriteWorkspaceSettings(false);
		configuration.setGradleDistribution(GradleDistributionWrapper.from(gradleConfig.getGradleDistribution()));
		configuration.setGradleUserHome(gradleConfig.getGradleUserHome());
		configuration.setApplyWorkingSets(false);
		configuration.setBuildScansEnabled(gradleConfig.isBuildScansEnabled());
		configuration.setOfflineMode(gradleConfig.isOffline());
		configuration.setAutoSync(true);

		BuildConfiguration buildConfig = configuration.toBuildConfig();

		GradleBuild build = CorePlugin.gradleWorkspaceManager().getGradleBuild(buildConfig);

		build.synchronize(NewProjectHandler.IMPORT_AND_MERGE, AsyncHandler.NO_OP);

		waitImport();

		return Status.OK_STATUS;
	}

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

	public static void refreshGradleProject(IProject project) {
		Optional<GradleBuild> optional = CorePlugin.gradleWorkspaceManager().getGradleBuild(project);

		GradleBuild build = optional.get();

		build.synchronize(NewProjectHandler.IMPORT_AND_MERGE);
	}

	public static void runGradleTask(IProject project, String task, IProgressMonitor monitor) throws CoreException {
		runGradleTask(project, new String[] {task}, monitor);
	}

	public static void runGradleTask(IProject project, String[] tasks, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration launchConfiguration =
			CorePlugin.gradleLaunchConfigurationManager().getOrCreateRunConfiguration(
				_getRunConfigurationAttributes(project, tasks));

		final ILaunchConfigurationWorkingCopy launchConfigurationWC = launchConfiguration.getWorkingCopy();

		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", Boolean.TRUE);
		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", Boolean.TRUE);
		launchConfigurationWC.setAttribute("org.eclipse.debug.ui.ATTR_PRIVATE", Boolean.TRUE);

		launchConfigurationWC.doSave();

		launchConfigurationWC.launch(ILaunchManager.RUN_MODE, monitor);
	}

	public static void waitImport() {
		IWorkspaceRoot root = null;

		try {
			ResourcesPlugin.getWorkspace().checkpoint(true);
			Job.getJobManager().join(CorePlugin.GRADLE_JOB_FAMILY, new NullProgressMonitor());
			Job.getJobManager().join(GradleCore.JOB_FAMILY_ID, new NullProgressMonitor());
			Thread.sleep(200);
			Job.getJobManager().beginRule(root = ResourcesPlugin.getWorkspace().getRoot(), null);
		}
		catch (InterruptedException ie) {
		}
		catch (IllegalArgumentException iae) {
		}
		catch (OperationCanceledException oce) {
		}
		finally {
			if (root != null) {
				Job.getJobManager().endRule(root);
			}
		}
	}

	private static GradleRunConfigurationAttributes _getRunConfigurationAttributes(IProject project, String[] tasks) {
		File rootDir = project.getLocation().toFile();

		BuildConfiguration buildConfig = CorePlugin.configurationManager().loadBuildConfiguration(rootDir);

		String projectDirectoryExpression = null;

		Optional<IProject> gradleProject = CorePlugin.workspaceOperations().findProjectByLocation(rootDir);

		if (gradleProject.isPresent()) {
			projectDirectoryExpression = ExpressionUtils.encodeWorkspaceLocation(gradleProject.get());
		}
		else {
			projectDirectoryExpression = rootDir.getAbsolutePath();
		}

		String gradleUserHome =
			buildConfig.getGradleUserHome() == null ? "" : buildConfig.getGradleUserHome().getAbsolutePath();

		List<String> taskList = new ArrayList<>();

		for (String task : tasks) {
			taskList.add(task);
		}

		String serializeString = GradleDistributionSerializer.INSTANCE.serializeToString(
			buildConfig.getGradleDistribution());

		return new GradleRunConfigurationAttributes(
			taskList, projectDirectoryExpression, serializeString, gradleUserHome, null,
			Collections.<String>emptyList(), Collections.<String>emptyList(), true, true,
			buildConfig.isOverrideWorkspaceSettings(), buildConfig.isOfflineMode(), buildConfig.isBuildScansEnabled());
	}

}