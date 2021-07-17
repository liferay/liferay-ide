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
import com.liferay.ide.server.core.portal.docker.IDockerServer;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public class LiferayGradleDockerServer implements IDockerServer {

	public LiferayGradleDockerServer() {
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
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject)) {
			LiferayGradleCore.logError("Can not find valid liferay workspace project.");
		}

		try {
			GradleUtil.runGradleTask(workspaceProject, new String[] {"createDockerContainer"}, monitor);
		}
		catch (CoreException exception) {
			LiferayGradleCore.logError(
				"Failed to create liferay docker container for project " + workspaceProject.getName(), exception);
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
	public void removeDockerContainer(IProgressMonitor monitor) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject)) {
			LiferayGradleCore.logError("Can not find valid liferay workspace project.");
		}

		try {
			GradleUtil.runGradleTask(workspaceProject, new String[] {"removeDockerContainer"}, monitor);
		}
		catch (CoreException exception) {
			LiferayGradleCore.logError(
				"Failed to remove liferay docker container for project " + workspaceProject.getName(), exception);
		}
	}

	@Override
	public void removeDockerImage(IProgressMonitor monitor) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject)) {
			LiferayGradleCore.logError("Can not find valid liferay workspace project.");
		}

		try {
			GradleUtil.runGradleTask(
				workspaceProject, new String[] {"cleanDockerImage"}, new String[] {"-x", "removeDockerContainer"},
				false, monitor);
		}
		catch (CoreException exception) {
			LiferayGradleCore.logError(
				"Failed to remove liferay docker image for project " + workspaceProject.getName(), exception);
		}
	}

	@Override
	public void startDockerContainer(IProgressMonitor monitor) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject)) {
			LiferayGradleCore.logError("Can not find valid liferay workspace project.");
		}

		try {
			GradleUtil.runGradleTask(
				workspaceProject, new String[] {"startDockerContainer"}, new String[] {"-x", "createDockerContainer"},
				false, monitor);
		}
		catch (CoreException exception) {
			LiferayGradleCore.logError(
				"Failed to start liferay docker container for project " + workspaceProject.getName(), exception);
		}
	}

	@Override
	public void stopDockerContainer(IProgressMonitor monitor) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject)) {
			LiferayGradleCore.logError("Can not find valid liferay workspace project.");
		}

		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"stopDockerContainer"}, monitor);
		}
		catch (CoreException exception) {
			LiferayGradleCore.logError(
				"Failed to stop liferay docker container for project " + workspaceProject.getName(), exception);
		}
	}

}