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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Charles Wu
 */
public class NewModuleExtOpMethods {

	public static final Status execute(NewModuleExtOp newModuleExtOp, ProgressMonitor pm) {
		IProgressMonitor progressMonitor = ProgressMonitorBridge.create(pm);

		progressMonitor.beginTask("Creating Liferay module ext project template files", 100);

		Status retval = null;

		Throwable errorStack = null;

		try {
			NewLiferayProjectProvider<BaseModuleOp> newLiferayProjectProvider = _getter.get(
				newModuleExtOp.getProjectProvider());

			IStatus status = newLiferayProjectProvider.createNewProject(newModuleExtOp, progressMonitor);

			retval = StatusBridge.create(status);

			if ((retval.severity() == Severity.ERROR) && (retval.exception() != null)) {
				errorStack = retval.exception();
			}
			else {
				copyFiles(newModuleExtOp);
			}
		}
		catch (Exception e) {
			errorStack = e;
		}

		if (errorStack != null) {
			String readableStack = CoreUtil.getStackTrace(errorStack);

			ProjectCore.logError(readableStack);

			return Status.createErrorStatus(readableStack + "\t Please see Eclipse error log for more details.");
		}

		return retval;
	}

	protected static void copyFiles(NewModuleExtOp moduleExtOp) {
		Job copyFilesJob = new Job("Copying files") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IPath projectFolder = PathBridge.create(SapphireUtil.getContent(moduleExtOp.getLocation()));

				IPath projecLocation = projectFolder.append(SapphireUtil.getContent(moduleExtOp.getProjectName()));

				ElementList<OverrideSourceEntry> overrideFiles = moduleExtOp.getOverrideFiles();

				List<String> relativePaths = new ArrayList<>(overrideFiles.size());

				overrideFiles.forEach(
					entry -> {
						relativePaths.add(SapphireUtil.getContent(entry.getValue()));
					});

				URI sourceJar = SapphireUtil.getContent(moduleExtOp.getSourceFileUri());

				if ((sourceJar == null) || relativePaths.isEmpty()) {
					return org.eclipse.core.runtime.Status.OK_STATUS;
				}

				Path sourceFolder = Paths.get(projecLocation.toString(), "src/main/java/");
				Path resourcesFolder = Paths.get(projecLocation.toString(), "src/main/resources/");

				try {
					ZipUtil.unzip(
						new File(sourceJar), sourceFolder.toFile(),
						path -> {

							// choose the folder which the file should go

							if (relativePaths.contains(path)) {
								if (path.startsWith("com/")) {
									return Pair.create(true, sourceFolder.toFile());
								}
								else {
									return Pair.create(true, resourcesFolder.toFile());
								}
							}
							else {
								return Pair.create(false, null);
							}
						});

					String projectName = SapphireUtil.getContent(moduleExtOp.getProjectName());

					IProject project = CoreUtil.getProject(projectName);

					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				catch (CoreException | IOException e) {
					ProjectCore.logError(e);
				}

				return org.eclipse.core.runtime.Status.OK_STATUS;
			}

		};

		copyFilesJob.schedule();
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {};

}