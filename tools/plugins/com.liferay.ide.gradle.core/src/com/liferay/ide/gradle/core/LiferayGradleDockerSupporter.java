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

import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.portal.docker.IDockerSupporter;
import com.liferay.ide.server.core.portal.docker.PortalDockerServerStreamsProxy.LiferayTaskProgressListener;

import java.io.ByteArrayOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public class LiferayGradleDockerSupporter implements IDockerSupporter {

	public LiferayGradleDockerSupporter() {
	}

	@Override
	public boolean canPublishModule(IServer server, IModule module) {
		IProject project = module.getProject();

		boolean inLiferayWorkspace = LiferayWorkspaceUtil.inLiferayWorkspace(project);

		boolean gradleProject = GradleUtil.isGradleProject(project);

		if (inLiferayWorkspace && gradleProject) {
			return true;
		}

		return false;
	}

	@Override
	public void createDockerContainer(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"createDockerContainer"},
				new String[] {"-x", "buildDockerImage"}, false, monitor);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dockerDeploy(IProject project, IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(project, new String[] {"dockerDeploy"}, monitor);
		}
		catch (Exception e) {
			LiferayGradleCore.logError(e);
		}
	}

	@Override
	public void logDockerContainer(IProject project, LiferayTaskProgressListener listener, IProgressMonitor monitor) {
		try {
			GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(project.getLocation().toFile());
			
			ProjectConnection connection = connector.connect();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			listener.setOutputStream(outputStream);
			
			connection.newBuild().addProgressListener(listener).setStandardOutput(outputStream).forTasks("logsDockerContainer").run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeDockerContainer(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"removeDockerContainer"}, new String[0],
				false, monitor);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startDockerContainer(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"startDockerContainer"},
				new String[] {"-x", "createDockerContainer", "-x", "buildDockerImage"}, false, monitor);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopDockerContainer(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"stopDockerContainer"}, monitor);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

}