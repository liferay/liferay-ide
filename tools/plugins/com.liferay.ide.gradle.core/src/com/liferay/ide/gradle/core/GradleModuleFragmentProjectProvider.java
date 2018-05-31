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
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOpMethods;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Terry Jia
 * @author Lovett Li
 */
public class GradleModuleFragmentProjectProvider
	extends AbstractLiferayProjectProvider implements NewLiferayProjectProvider<NewModuleFragmentOp> {

	public GradleModuleFragmentProjectProvider() {
		super(null);
	}

	@Override
	public IStatus createNewProject(NewModuleFragmentOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		String projectName = op.getProjectName().content();
		IPath location = PathBridge.create(op.getLocation().content());

		String[] bsnAndVersion = NewModuleFragmentOpMethods.getBsnAndVersion(op);

		String bundleSymbolicName = bsnAndVersion[0];
		String version = bsnAndVersion[1];

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("");
		sb.append("-d \"");
		sb.append(location.toFile().getAbsolutePath());
		sb.append("\" ");
		sb.append("");
		sb.append("-t ");
		sb.append("");
		sb.append("fragment ");
		sb.append("");

		if (!bundleSymbolicName.equals("")) {
			sb.append("-h ");
			sb.append(bundleSymbolicName);
			sb.append(" ");
		}

		if (!version.equals("")) {
			sb.append("-H ");
			sb.append(version);
			sb.append(" ");
		}

		sb.append("\"");
		sb.append(projectName);
		sb.append("\" ");

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (Exception e) {
			return GradleCore.createErrorStatus("Could not create module fragment project.", e);
		}

		NewModuleFragmentOpMethods.copyOverrideFiles(op);

		IPath projecLocation = location.append(projectName);

		boolean hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
		boolean useDefaultLocation = op.getUseDefaultLocation().content(true);
		boolean inWorkspacePath = false;

		final IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (hasGradleWorkspace && (liferayWorkspaceProject != null) && !useDefaultLocation) {
			IPath workspaceLocation = liferayWorkspaceProject.getLocation();

			if (workspaceLocation != null) {
				String liferayWorkspaceProjectModulesDir = LiferayWorkspaceUtil.getModulesDir(liferayWorkspaceProject);

				if (liferayWorkspaceProjectModulesDir != null) {
					IPath modulesPath = workspaceLocation.append(liferayWorkspaceProjectModulesDir);

					if (modulesPath.isPrefixOf(projecLocation)) {
						inWorkspacePath = true;
					}
				}
			}
		}

		if ((hasGradleWorkspace && useDefaultLocation) || inWorkspacePath) {
			GradleUtil.refreshGradleProject(liferayWorkspaceProject);
		}
		else {
			GradleUtil.importGradleProject(projecLocation, monitor);
		}

		return retval;
	}

	@Override
	public synchronized ILiferayProject provide(Object adaptable) {
		return null;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		if (path != null) {
			if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(path.toOSString())) {
				retval = GradleCore.createErrorStatus(" Can't set WorkspaceProject root folder as project directory. ");
			}
		}

		return retval;
	}

}