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

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.buildship.core.GradleBuild;
import org.eclipse.buildship.core.GradleCore;
import org.eclipse.buildship.core.GradleWorkspace;
import org.eclipse.buildship.core.internal.configuration.BuildConfiguration;
import org.eclipse.buildship.core.internal.configuration.GradleProjectNature;
import org.eclipse.buildship.core.internal.configuration.GradleProjectNatureConfiguredEvent;
import org.eclipse.buildship.core.internal.configuration.ProjectConfiguration;
import org.eclipse.buildship.core.internal.configuration.RunConfiguration;
import org.eclipse.buildship.core.internal.console.ProcessDescription;
import org.eclipse.buildship.core.internal.event.Event;
import org.eclipse.buildship.core.internal.event.EventListener;
import org.eclipse.buildship.core.internal.launch.ExecuteLaunchRequestEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.gradle.tooling.LongRunningOperation;
import org.gradle.tooling.events.OperationDescriptor;
import org.gradle.tooling.events.ProgressEvent;
import org.gradle.tooling.events.ProgressListener;
import org.gradle.tooling.events.task.TaskFinishEvent;
import org.gradle.tooling.model.GradleProject;

/**
 * @author Andy Wu
 * @author Charles Wu
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class GradleProjectListener implements EventListener {

	@Override
	public void onEvent(Event event) {
		if (event instanceof GradleProjectNatureConfiguredEvent) {
			GradleProjectNatureConfiguredEvent createEvent = (GradleProjectNatureConfiguredEvent)event;

			IProject project = createEvent.getProject();

			try {
				_configureIfLiferayProject(project);
			}
			catch (CoreException ce) {
				LiferayGradleCore.logError("config project " + project.getName() + "error", ce);
			}
		}

		if (event instanceof ExecuteLaunchRequestEvent) {
			ExecuteLaunchRequestEvent launchEvent = (ExecuteLaunchRequestEvent)event;

			ProcessDescription processDescription = launchEvent.getProcessDescription();

			RunConfiguration runConfig = processDescription.getRunConfig();

			ProjectConfiguration projectConfiguration = runConfig.getProjectConfiguration();

			BuildConfiguration buildConfiguration = projectConfiguration.getBuildConfiguration();

			GradleWorkspace workspace = GradleCore.getWorkspace();

			GradleBuild createBuild = workspace.createBuild(buildConfiguration.toApiBuildConfiguration());

			try {
				GradleProject model = createBuild.withConnection(
					connection -> {
						return connection.getModel(GradleProject.class);
					},
					new NullProgressMonitor());

				LongRunningOperation operation = launchEvent.getOperation();

				List<String> tasksName = runConfig.getTasks();

				Stream<String> taskNameStream = tasksName.stream();

				Optional<String> taskNameOpt = taskNameStream.filter(
					task -> _needSynchronizedTaskList.contains(task)
				).findFirst();

				if (taskNameOpt.isPresent()) {
					String taskName = taskNameOpt.get();

					operation.addProgressListener(
						new ProgressListener() {

							@Override
							public void statusChanged(ProgressEvent progressEvent) {
								if (progressEvent instanceof TaskFinishEvent) {
									OperationDescriptor descriptor = progressEvent.getDescriptor();

									String progressDispalyName = descriptor.getName();

									if (progressDispalyName.contains(taskName)) {
										IProject project = CoreUtil.getProject(model.getName());

										if (project != null) {
											GradleUtil.refreshProject(project);
										}
									}
								}
							}

						});
				}
			}
			catch (Exception e) {
				LiferayGradleCore.logError(e);
			}
		}
	}

	private void _configureIfLiferayProject(final IProject project) throws CoreException {
		if (GradleProjectNature.isPresentOn(project) && !LiferayNature.hasNature(project)) {
			final boolean[] needAddNature = new boolean[1];

			needAddNature[0] = false;

			IFile bndFile = project.getFile("bnd.bnd");

			// case 1: has bnd file

			if (FileUtil.exists(bndFile)) {
				needAddNature[0] = true;
			}
			else if (ProjectUtil.isWorkspaceWars(project)) {
				needAddNature[0] = true;
			}
			else {
				IFile gulpFile = project.getFile("gulpfile.js");

				if (FileUtil.exists(gulpFile)) {
					String gulpFileContent;

					File file = FileUtil.getFile(gulpFile);

					try {
						gulpFileContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

						// case 2: has gulpfile.js with some content

						if (gulpFileContent.contains("require('liferay-theme-tasks')")) {
							needAddNature[0] = true;
						}
					}
					catch (IOException ioe) {
						LiferayGradleCore.logError("read gulpfile.js file fail", ioe);
					}
				}
			}

			try {
				IProgressMonitor monitor = new NullProgressMonitor();

				if (needAddNature[0]) {
					LiferayNature.addLiferayNature(project, monitor);

					return;
				}

				final CustomModel customModel = LiferayGradleCore.getToolingModel(CustomModel.class, project);

				if (customModel == null) {
					throw new CoreException(
						LiferayGradleCore.createErrorStatus("Unable to get read gradle configuration"));
				}

				if (customModel.isLiferayModule() || customModel.hasPlugin("org.gradle.api.plugins.WarPlugin") ||
					customModel.hasPlugin("com.liferay.gradle.plugins.theme.builder.ThemeBuilderPlugin")) {

					LiferayNature.addLiferayNature(project, monitor);
				}
			}
			catch (Exception e) {
				LiferayGradleCore.logError("Unable to get tooling model", e);
			}
		}
	}

	private static List<String> _needSynchronizedTaskList = Arrays.asList(
		"buildService", "buildCSS", "buildDB", "buildExtInfo", "buildLang", "buildTagLib", "buildTheme", "buildWSDD",
		"downloadNode", "executeBnd", "executeNode", "executeNodeScript", "mergeFiles", "configJSModules",
		"npmRunBuild", "buildSoy", "buildNeeded", "initBundle");

}