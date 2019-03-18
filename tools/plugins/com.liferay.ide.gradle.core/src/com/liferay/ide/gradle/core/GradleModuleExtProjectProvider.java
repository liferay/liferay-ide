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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.ext.NewModuleExtOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;

import org.osgi.framework.Version;

/**
 * @author Charles Wu
 * @author Simon Jiang
 */
public class GradleModuleExtProjectProvider
	extends AbstractLiferayProjectProvider
	implements NewLiferayProjectProvider<NewModuleExtOp>, SapphireContentAccessor {

	public GradleModuleExtProjectProvider() {
		super(null);
	}

	@Override
	public IStatus createNewProject(NewModuleExtOp op, IProgressMonitor monitor) throws CoreException {
		IStatus status = Status.OK_STATUS;

		String projectName = get(op.getProjectName());
		String originalModuleName = get(op.getOriginalModuleName());
		Version originalModuleVersion = get(op.getOriginalModuleVersion());
		String targetPlatform = get(op.getTargetPlatformVersion());

		IPath location = PathBridge.create(get(op.getLocation()));
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		StringBuilder sb = new StringBuilder();

		File locationFile = location.toFile();

		sb.append("create -d \"");
		sb.append(locationFile.getAbsolutePath());

		if (workspaceProject != null) {
			sb.append("\" ");
			sb.append("--base \"");

			IPath workspaceLocation = workspaceProject.getLocation();

			sb.append(workspaceLocation.toOSString());
		}

		sb.append("\" -t ");
		sb.append("modules-ext ");
		sb.append("-m ");
		sb.append(originalModuleName);

		if (CoreUtil.isNullOrEmpty(targetPlatform)) {
			sb.append(" -M ");
			sb.append(originalModuleVersion);
		}

		sb.append(" \"");
		sb.append(projectName);
		sb.append("\"");

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (Exception e) {
			return LiferayGradleCore.createErrorStatus("Could not create module ext project.", e);
		}

		IPath projecLocation = location.append(projectName);

		CoreUtil.openProject(projectName, projecLocation, monitor);

		if (LiferayWorkspaceUtil.inLiferayWorkspace(projecLocation)) {
			GradleUtil.refreshProject(workspaceProject);
		}
		else {
			GradleUtil.synchronizeProject(projecLocation, monitor);
		}

		return status;
	}

	@Override
	public synchronized ILiferayProject provide(Class<?> type, Object adaptable) {
		return null;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(path)) {
			retval = LiferayGradleCore.createErrorStatus("Invalid location for workspace project.");
		}

		return retval;
	}

}