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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.Pair;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
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

			if ((retval.severity() == Status.Severity.ERROR) && (retval.exception() != null)) {
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
		URI sourceJar = _getter.get(moduleExtOp.getSourceFileURI());

		if (sourceJar == null) {
			return;
		}

		ElementList<OverrideSourceEntry> overrideFiles = moduleExtOp.getOverrideFiles();

		Stream<OverrideSourceEntry> stream = overrideFiles.stream();

		List<String> relativePaths = stream.map(
			element -> element.getValue()
		).map(
			_getter::get
		).collect(
			Collectors.toList()
		);

		if (relativePaths.isEmpty()) {
			return;
		}

		IPath projectFolder = PathBridge.create(_getter.get(moduleExtOp.getLocation()));

		IPath projecLocation = projectFolder.append(_getter.get(moduleExtOp.getProjectName()));

		File sourceFolder = FileUtil.getFile(projecLocation.append("src/main/java/"));

		try {
			ZipUtil.unzip(
				new File(sourceJar), sourceFolder,
				path -> {
					boolean containsPath = relativePaths.contains(path);

					File file = null;

					if (containsPath) {
						if (path.startsWith("com/")) {
							file = sourceFolder;
						}
						else {
							file = FileUtil.getFile(projecLocation.append("src/main/resources/"));
						}
					}

					return new Pair<>(containsPath, file);
				});
		}
		catch (IOException ioe) {
			ProjectCore.logError(ioe);
		}

		String projectName = _getter.get(moduleExtOp.getProjectName());

		IProject project = CoreUtil.getProject(projectName);

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (CoreException ce) {
		}
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}