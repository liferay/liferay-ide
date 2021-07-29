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

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;

import com.google.common.collect.Lists;

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.portal.docker.IDockerServer;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.RuntimeWorkingCopy;

import org.gradle.tooling.model.GradleProject;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
@SuppressWarnings("restriction")
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
		try {
			IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			if (Objects.isNull(workspaceProject)) {
				LiferayGradleCore.logError("Can not find valid liferay workspace project.");
			}

			String[] tasks = {"removeDockerContainer", "cleanDockerImage", "createDockerContainer"};

			monitor.beginTask("startDockerContainer", 100);

			monitor.worked(20);

			ArrayList<String> ignorTasks = Lists.newArrayList();

			List<IProject> warCoreExtProjects = _getWarCoreExtModules();

			if (ListUtil.isNotEmpty(warCoreExtProjects)) {
				for (IProject project : warCoreExtProjects) {
					GradleProject gradleProject = GradleUtil.getGradleProject(project);

					if (Objects.nonNull(gradleProject)) {
						ignorTasks.add("-x");
						ignorTasks.add(gradleProject.getPath() + ":buildExtInfo");
						ignorTasks.add("-x");
						ignorTasks.add(gradleProject.getPath() + ":deploy");
						ignorTasks.add("-x");
						ignorTasks.add(gradleProject.getPath() + ":dockerDeploy");
					}
				}
			}

			GradleUtil.runGradleTask(workspaceProject, tasks, ignorTasks.toArray(new String[0]), false, monitor);

			monitor.worked(40);

			ProjectInfo projectInfo = LiferayGradleCore.getToolingModel(ProjectInfo.class, workspaceProject);

			if ((projectInfo != null) && CoreUtil.isNotNullOrEmpty(projectInfo.getDockerImageId()) &&
				CoreUtil.isNotNullOrEmpty(projectInfo.getDockerContainerId())) {

				PortalDockerRuntime dockerRuntime = Stream.of(
					ServerCore.getRuntimes()
				).filter(
					runtime -> Objects.nonNull(runtime)
				).map(
					runtime -> (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, monitor)
				).filter(
					runtime -> Objects.nonNull(runtime)
				).filter(
					runtime -> Objects.equals(
						projectInfo.getDockerImageId(), String.join(":", runtime.getImageRepo(), runtime.getImageTag()))
				).findAny(
				).orElseGet(
					null
				);

				if (Objects.nonNull(dockerRuntime)) {
					Image dockerImage = LiferayDockerClient.getDockerImageByName(projectInfo.getDockerImageId());

					if (Objects.isNull(dockerImage)) {
						return;
					}

					IRuntime runtime = dockerRuntime.getRuntime();

					IRuntimeWorkingCopy iruntimeWorkingCopy = runtime.createWorkingCopy();

					RuntimeWorkingCopy runtimeWorkingCopy = (RuntimeWorkingCopy)iruntimeWorkingCopy.loadAdapter(
						RuntimeWorkingCopy.class, monitor);

					runtimeWorkingCopy.setAttribute(PortalDockerRuntime.PROP_DOCKER_IMAGE_ID, dockerImage.getId());

					runtimeWorkingCopy.save(true, monitor);
				}

				PortalDockerServer dockerServer = Stream.of(
					ServerCore.getServers()
				).filter(
					server -> Objects.nonNull(server)
				).map(
					server -> (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, monitor)
				).filter(
					server -> Objects.nonNull(server)
				).filter(
					server -> Objects.equals(projectInfo.getDockerContainerId(), server.getContainerName())
				).findAny(
				).orElseGet(
					null
				);

				if (Objects.nonNull(dockerServer)) {
					Container dockerContainer = LiferayDockerClient.getDockerContainerByName(
						projectInfo.getDockerContainerId());

					if (Objects.isNull(dockerContainer)) {
						return;
					}

					IServer server = dockerServer.getServer();

					IServerWorkingCopy serverWorkingCopy = server.createWorkingCopy();

					serverWorkingCopy.setAttribute(PortalDockerServer.DOCKER_CONTAINER_ID, dockerContainer.getId());

					serverWorkingCopy.save(true, monitor);
				}

				monitor.worked(60);

				GradleUtil.runGradleTask(
					workspaceProject, new String[] {"startDockerContainer"},
					new String[] {"-x", "createDockerContainer"}, false, monitor);

				monitor.done();
			}
		}
		catch (Exception exception) {
			LiferayGradleCore.logError("Failed to start docker server", exception);
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

	private List<IProject> _getWarCoreExtModules() {
		IWorkspaceProject workspace = LiferayWorkspaceUtil.getGradleWorkspaceProject();

		Set<IProject> childProjects = workspace.getChildProjects();

		Stream<IProject> projectsStream = childProjects.stream();

		return projectsStream.filter(
			project -> Objects.nonNull(project)
		).filter(
			project -> {
				IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

				return bundleProject.isWarCoreExtModule();
			}
		).collect(
			Collectors.toList()
		);
	}

}