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

package com.liferay.ide.gradle.action;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;

import com.google.common.collect.Lists;

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.server.util.ServerUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public class InitDockerBundleTaskAction extends GradleTaskAction {

	public void run(IAction action) {
		CompletableFuture<ProjectInfo> projectInfoAsync = CompletableFuture.supplyAsync(
			() -> {
				_projectInfo = LiferayGradleCore.getToolingModel(ProjectInfo.class, project);

				return _projectInfo;
			});

		projectInfoAsync.thenAcceptAsync(
			projectInfo -> {
				if ((projectInfo != null) && CoreUtil.isNotNullOrEmpty(projectInfo.getDockerImageId()) &&
					CoreUtil.isNotNullOrEmpty(projectInfo.getDockerContainerId())) {

					super.run(action);
				}
				else {
					LiferayGradleCore.log(
						LiferayGradleCore.createErrorStatus("Please check liferay gradle workspace plugin setting."));
				}
			});
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		action.setEnabled(LiferayWorkspaceUtil.isValidWorkspace(project));
	}

	@Override
	protected void afterAction() {
		_buildUpWorkspaceDockerServerAndRuntime();
	}

	@Override
	protected void beforeAction() {
		_cleanUpWorkspaceDockerServerAndRuntime();
	}

	@Override
	protected String getGradleTaskName() {
		return "createDockerContainer";
	}

	@Override
	protected List<String> getGradleTasks() {
		return Lists.newArrayList("removeDockerContainer", "cleanDockerImage", "createDockerContainer");
	}

	private void _buildUpWorkspaceDockerServerAndRuntime() {
		try {
			Image image = LiferayDockerClient.getDockerImageByName(_projectInfo.getDockerImageId());

			if (Objects.isNull(image)) {
				LiferayGradleCore.logError(
					"Failed to verify docker image for project " + _projectInfo.getDockerImageId());

				return;
			}

			String imageRepoTag = Stream.of(
				image.getRepoTags()
			).filter(
				repoTag -> repoTag.equals(_projectInfo.getDockerImageId())
			).findFirst(
			).orElse(
				null
			);

			IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

			if ((project != null) && (imageRepoTag != null)) {
				IRuntimeType portalRuntimeType = ServerCore.findRuntimeType(PortalDockerRuntime.ID);

				IRuntimeWorkingCopy runtimeWC = portalRuntimeType.createRuntime(portalRuntimeType.getName(), null);

				ServerUtil.setRuntimeName(runtimeWC, -1, project.getName());

				PortalDockerRuntime portalDockerRuntime = (PortalDockerRuntime)runtimeWC.loadAdapter(
					PortalDockerRuntime.class, null);

				String dockerImageId = _projectInfo.getDockerImageId();

				portalDockerRuntime.setImageRepo(dockerImageId.split(":")[0]);

				portalDockerRuntime.setImageId(image.getId());

				portalDockerRuntime.setImageTag(dockerImageId.split(":")[1]);

				runtimeWC.save(true, null);

				IServerType serverType = ServerCore.findServerType(PortalDockerServer.ID);

				IServerWorkingCopy serverWC = serverType.createServer(serverType.getName(), null, runtimeWC, null);

				serverWC.setName(serverType.getName() + " " + project.getName());

				Container container = LiferayDockerClient.getDockerContainerByName(_projectInfo.getDockerContainerId());

				PortalDockerServer portalDockerServer = (PortalDockerServer)serverWC.loadAdapter(
					PortalDockerServer.class, null);

				portalDockerServer.setContainerName(_projectInfo.getDockerContainerId());

				portalDockerServer.setContainerId(container.getId());

				portalDockerServer.setImageId(portalDockerRuntime.getImageId());

				serverWC.save(true, null);
			}
		}
		catch (Exception e) {
			LiferayGradleCore.logError("Failed to add server and runtime", e);
		}
	}

	private void _cleanUpWorkspaceDockerServerAndRuntime() {
		IServer[] servers = ServerCore.getServers();

		try {
			for (IServer server : servers) {
				PortalDockerServer portalDockerServer = (PortalDockerServer)server.loadAdapter(
					PortalDockerServer.class, null);

				if (Objects.nonNull(portalDockerServer) &&
					Objects.equals(_projectInfo.getDockerContainerId(), portalDockerServer.getContainerName())) {

					server.delete();
				}
			}
		}
		catch (Exception exception) {
			LiferayServerCore.logError("Failed to remove docker server", exception);
		}

		IRuntime[] runtimes = ServerCore.getRuntimes();

		try {
			for (IRuntime runtime : runtimes) {
				PortalDockerRuntime portalDockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(
					PortalDockerRuntime.class, null);

				if (Objects.nonNull(portalDockerRuntime) &&
					Objects.equals(
						_projectInfo.getDockerImageId(),
						String.join(":", portalDockerRuntime.getImageRepo(), portalDockerRuntime.getImageTag()))) {

					runtime.delete();
				}
			}
		}
		catch (Exception exception) {
			LiferayServerCore.logError("Failed to remove docker runtime", exception);
		}
	}

	private ProjectInfo _projectInfo;

}