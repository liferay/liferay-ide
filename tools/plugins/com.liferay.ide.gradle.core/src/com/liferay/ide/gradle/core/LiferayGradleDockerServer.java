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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.portal.docker.IDockerServer;

import java.io.File;

import java.net.InetAddress;

import org.apache.commons.lang.SystemUtils;

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

	public static String getDefaultDockerUrl() {
		String dockerUrl = System.getenv("DOCKER_HOST");

		if (CoreUtil.isNullOrEmpty(dockerUrl)) {
			if (SystemUtils.IS_OS_UNIX && new File("/var/run/docker.sock").exists()) {
				dockerUrl = new String("unix:///var/run/docker.sock");
			}
			else {
				if (SystemUtils.IS_OS_WINDOWS) {
					if (SystemUtils.IS_OS_WINDOWS_7) {
						try {
							InetAddress inetAddress = InetAddress.getLocalHost();

							dockerUrl = "tcp://" + inetAddress.getHostAddress() + ":2376";
						}
						catch (Exception e) {
							dockerUrl = "tcp://127.0.0.1:2376";
						}
					}
					else {
						dockerUrl = "tcp://127.0.0.1:2375";
					}
				}
				else {
					dockerUrl = "tcp://127.0.0.1:2375";
				}
			}
		}

		return dockerUrl;
	}

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
	public void cleanDockerImage(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"cleanDockerImage"},
				new String[] {"-x", "removeDockerContainer"}, false, monitor);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createDockerContainer(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"createDockerContainer"}, monitor);
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
	public void removeDockerContainer(IProgressMonitor monitor) {
		try {
			GradleUtil.runGradleTask(
				LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"removeDockerContainer"}, monitor);
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
				new String[] {"-x", "createDockerContainer"}, false, monitor);
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