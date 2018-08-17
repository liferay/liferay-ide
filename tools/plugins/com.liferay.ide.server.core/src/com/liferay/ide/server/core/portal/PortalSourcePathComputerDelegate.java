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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourcePathComputer;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalSourcePathComputerDelegate extends JavaSourcePathComputer {

	public static final String ID = "com.liferay.ide.server.core.portal.sourcePathComputer";

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
		throws CoreException {

		List<ISourceContainer> sourceContainers = new ArrayList<>();

		IServer server = ServerUtil.getServer(configuration);

		PortalServerBehavior behavior = (PortalServerBehavior)server.loadAdapter(PortalServerBehavior.class, monitor);

		Set<IProject> watchProjects = behavior.getWatchProjects();

		Stream<IProject> stream = watchProjects.stream();

		stream.map(
			project ->  JavaCore.create(project)
		).filter(
			javaProject -> javaProject != null
		).forEach(
			javaProject -> _addSourceContainers(configuration, monitor, sourceContainers, javaProject.getProject())
		);

		Stream.of(
			CoreUtil.getAllProjects()
		).map(
			project -> LiferayCore.create(IWorkspaceProject.class, project)
		).filter(
			workspaceProject -> workspaceProject != null
		).flatMap(
			workspaceProject -> workspaceProject.getChildProjects().stream()
		).forEach(
			liferayProject -> _addSourceContainers(
				configuration, monitor, sourceContainers, liferayProject.getProject())
		);

		IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, server);

		if (workspaceProject != null) {
			IProject project = workspaceProject.getProject();

			try {
				IJavaProject javaProject = JavaCore.create(project);

				if ((javaProject != null) && javaProject.isOpen()) {
					_addSourceContainers(configuration, monitor, sourceContainers, workspaceProject.getProject());
				}
			}
			catch (Exception e) {
			}
		}

		Stream.of(
			server.getModules()
		).map(
			module -> LiferayCore.create(module.getProject())
		).filter(
			liferayProject -> liferayProject != null
		).forEach(
			liferayProject -> _addSourceContainers(
				configuration, monitor, sourceContainers, liferayProject.getProject())
		);

		return sourceContainers.toArray(new ISourceContainer[0]);
	}

	@Override
	public String getId() {
		return ID;
	}

	private void _addSourceContainers(
		ILaunchConfiguration configuration, IProgressMonitor monitor, List<ISourceContainer> sourceContainers,
		IProject project) {

		String projectName = project.getName();

		try {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();

			ILaunchManager manager = debugPlugin.getLaunchManager();

			ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType(
				"org.eclipse.jdt.launching.localJavaApplication");

			ILaunchConfigurationWorkingCopy sourceLookupConfig = launchConfigurationType.newInstance(
				null, configuration.getName());

			sourceLookupConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);

			ISourceContainer[] computedSourceContainers = super.computeSourceContainers(sourceLookupConfig, monitor);

			Stream.of(
				computedSourceContainers
			).filter(
				computedSourceContainer -> !sourceContainers.contains(computedSourceContainer)
			).forEach(
				sourceContainers::add
			);
		}
		catch (CoreException ce) {
			LiferayServerCore.logError("Unable to add source container for project " + projectName, ce);
		}
	}

}