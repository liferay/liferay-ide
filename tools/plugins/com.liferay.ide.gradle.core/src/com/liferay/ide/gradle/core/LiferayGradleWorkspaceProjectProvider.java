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
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Optional;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.server.core.IServer;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andy Wu
 * @author Terry Jia
 * @author Charles Wu
 * @author Simon Jiang
 */
@Component(property = "type=gradle_workspace", service = NewLiferayWorkspaceProjectProvider.class)
public class LiferayGradleWorkspaceProjectProvider
	extends AbstractLiferayProjectProvider
	implements NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp>, SapphireContentAccessor {

	public LiferayGradleWorkspaceProjectProvider() {
		super(new Class<?>[] {IProject.class, IServer.class});
	}

	@Override
	public IStatus createNewProject(NewLiferayWorkspaceOp op, IProgressMonitor monitor) throws CoreException {
		Value<Path> locationPath = op.getLocation();

		IPath location = PathBridge.create(locationPath.content());

		String workspaceName = get(op.getWorkspaceName());

		IPath workspaceLocation = location.append(workspaceName);

		StringBuilder sb = new StringBuilder();

		sb.append("--base ");
		sb.append("\"");
		sb.append(workspaceLocation.toOSString());
		sb.append("\" ");
		sb.append("init ");
		sb.append("-v ");
		sb.append(get(op.getLiferayVersion()));

		try {
			BladeCLI.execute(sb.toString());
		}
		catch (BladeCLIException bclie) {
			return ProjectCore.createErrorStatus(bclie);
		}

		boolean enableTargetPlatform = get(op.getEnableTargetPlatform());

		if (enableTargetPlatform) {
			try {
				PropertiesConfiguration config = new PropertiesConfiguration(
					FileUtil.getFile(workspaceLocation.append("gradle.properties")));

				config.setProperty(WorkspaceConstants.TARGET_PLATFORM_VERSION_PROPERTY, get(op.getTargetPlatform()));
				config.setProperty(
					WorkspaceConstants.TARGET_PLATFORM_INDEX_SOURCES_PROPERTY, get(op.getIndexSources()));

				config.save();
			}
			catch (ConfigurationException ce) {
				LiferayGradleCore.logError(ce);
			}
		}

		IPath wsLocation = location.append(workspaceName);

		IStatus importProjectStatus = importProject(wsLocation, monitor);

		if (importProjectStatus != Status.OK_STATUS) {
			return importProjectStatus;
		}

		boolean initBundle = get(op.getProvisionLiferayBundle());

		if (initBundle) {
			String bundleUrl = get(op.getBundleUrl());

			String serverName = get(op.getServerName());

			initBundle(bundleUrl, serverName, workspaceName);
		}

		return Status.OK_STATUS;
	}

	@Override
	public String getInitBundleUrl(String workspaceLocation) {
		return LiferayWorkspaceUtil.getGradleProperty(
			workspaceLocation, WorkspaceConstants.BUNDLE_URL_PROPERTY, WorkspaceConstants.BUNDLE_URL_CE_7_0);
	}

	@Override
	public IStatus importProject(IPath wsLocation, IProgressMonitor monitor) {
		try {
			CoreUtil.openProject(wsLocation.lastSegment(), wsLocation, monitor);
		}
		catch (CoreException ce) {
			return ProjectCore.createErrorStatus(ce);
		}

		return GradleUtil.synchronizeProject(wsLocation, monitor);
	}

	@Override
	public synchronized ILiferayProject provide(Class<?> type, Object adaptable) {
		if (!type.isAssignableFrom(LiferayGradleWorkspaceProject.class)) {
			return null;
		}

		if (adaptable instanceof IProject) {
			final IProject project = (IProject)adaptable;

			if (GradleUtil.isGradleProject(project) && LiferayWorkspaceUtil.isValidWorkspace(project)) {
				return new LiferayGradleWorkspaceProject(project);
			}
		}

		return Optional.ofNullable(
			adaptable
		).filter(
			i -> i instanceof IServer
		).map(
			IServer.class::cast
		).map(
			ServerUtil::getLiferayRuntime
		).map(
			liferayRuntime -> liferayRuntime.getLiferayHome()
		).map(
			LiferayGradleWorkspaceProjectProvider::_getWorkspaceProjectFromLiferayHome
		).orElse(
			null
		);
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		// TODO validation gradle project location

		return retval;
	}

	private static IWorkspaceProject _getWorkspaceProjectFromLiferayHome(final IPath liferayHome) {
		return Optional.ofNullable(
			LiferayWorkspaceUtil.getWorkspaceProject()
		).filter(
			workspaceProject -> {
				IPath workspaceProjectLocation = workspaceProject.getRawLocation();

				if (workspaceProjectLocation == null) {
					return false;
				}

				return workspaceProjectLocation.isPrefixOf(liferayHome);
			}
		).map(
			workspaceProject -> LiferayCore.create(IWorkspaceProject.class, workspaceProject)
		).orElse(
			null
		);
	}

}