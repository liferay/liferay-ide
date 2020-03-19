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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Joye Luo
 * @author Andy Wu
 * @author Terry Jia
 * @author Seiphon Wang
 */
public class LiferayMavenWorkspaceProjectProvider
	extends LiferayMavenProjectProvider implements NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> {

	@Override
	public IStatus createNewProject(NewLiferayWorkspaceOp op, IProgressMonitor monitor) throws CoreException {
		Value<Path> locationValue = op.getLocation();

		IPath location = PathBridge.create(locationValue.content());

		Value<String> workspaceNameValue = op.getWorkspaceName();

		String workspaceName = workspaceNameValue.content();

		Value<String> version = op.getLiferayVersion();

		StringBuilder sb = new StringBuilder();

		sb.append("--base ");
		sb.append("\"");
		sb.append(location.toOSString());
		sb.append("\" ");
		sb.append("init ");
		sb.append("-b ");
		sb.append("maven ");
		sb.append("-v ");
		sb.append(version.content());

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (BladeCLIException bclie) {
			return ProjectCore.createErrorStatus(bclie);
		}

		IStatus importProjectStatus = importProject(location, monitor);

		if (importProjectStatus != Status.OK_STATUS) {
			return importProjectStatus;
		}

		Value<Boolean> initBundle = op.getProvisionLiferayBundle();

		if (initBundle.content()) {
			Value<String> bundleUrl = op.getBundleUrl();

			Value<String> serverName = op.getServerName();

			initBundle(bundleUrl.content(), serverName.content(), workspaceName);
		}

		return Status.OK_STATUS;
	}

	@Override
	public String getInitBundleUrl(String workspaceLocation) {
		return LiferayWorkspaceUtil.getMavenProperty(
			workspaceLocation, WorkspaceConstants.BUNDLE_URL_PROPERTY, WorkspaceConstants.BUNDLE_URL_CE_7_0);
	}

	@Override
	public IStatus importProject(IPath workspaceLocation, IProgressMonitor monitor) {
		try {
			String wsName = workspaceLocation.lastSegment();

			CoreUtil.openProject(wsName, workspaceLocation, monitor);
			MavenUtil.updateProjectConfiguration(wsName, workspaceLocation.toOSString(), monitor);
		}
		catch (Exception ce) {
			return ProjectCore.createErrorStatus(ce);
		}

		return Status.OK_STATUS;
	}

	@Override
	public ILiferayProject provide(Class<?> type, Object adaptable) {
		if (!type.isAssignableFrom(LiferayMavenWorkspaceProject.class)) {
			return null;
		}

		if (adaptable instanceof IProject) {
			final IProject project = (IProject)adaptable;

			try {
				if (MavenUtil.isMavenProject(project) && LiferayWorkspaceUtil.isValidWorkspace(project)) {
					return new LiferayMavenWorkspaceProject(project);
				}
			}
			catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		// TODO validation maven project location

		return retval;
	}

}