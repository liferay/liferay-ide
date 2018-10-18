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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.eclipse.buildship.core.internal.configuration.GradleProjectNature;
import org.eclipse.buildship.core.internal.configuration.GradleProjectNatureConfiguredEvent;
import org.eclipse.buildship.core.internal.event.Event;
import org.eclipse.buildship.core.internal.event.EventListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Andy Wu
 * @author Charles Wu
 */
@SuppressWarnings("restriction")
public class GradleProjectCreatedListener implements EventListener {

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

}