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

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;

import com.google.common.collect.Lists;

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.core.LiferayGradleDockerSupporter;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.docker.IDockerSupporter;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.server.util.ServerUtil;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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
		_projectInfo = LiferayGradleCore.getToolingModel(ProjectInfo.class, project);

		if ((_projectInfo != null) && CoreUtil.isNotNullOrEmpty(_projectInfo.getDockerImageId()) &&
			CoreUtil.isNotNullOrEmpty(_projectInfo.getDockerContainerId())) {

			super.run(action);
		}
		else {
			LiferayGradleCore.log(
				LiferayGradleCore.createErrorStatus("Please check liferay gradle workspace plugin setting."));
		}
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

	private void _buildUpWorkspaceDockerServerAndRuntime() {
		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withShowAll(true);

			List<Image> images = listImagesCmd.exec();

			Iterator<Image> iterator = images.iterator();

			while (iterator.hasNext()) {
				Image image = iterator.next();

				String imageRepoTag = image.getRepoTags()[0];

				if ((imageRepoTag != null) && imageRepoTag.equals(_projectInfo.getDockerImageId())) {
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

					ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();

					listContainersCmd.withNameFilter(Lists.newArrayList(_projectInfo.getDockerContainerId()));
					listContainersCmd.withLimit(1);

					List<Container> containers = listContainersCmd.exec();

					if (ListUtil.isEmpty(containers)) {
						return;
					}

					Container container = containers.get(0);

					PortalDockerServer portalDockerServer = (PortalDockerServer)serverWC.loadAdapter(
						PortalDockerServer.class, null);

					portalDockerServer.setContainerName(_projectInfo.getDockerContainerId());

					portalDockerServer.setContainerId(container.getId());

					portalDockerServer.setImageId(portalDockerRuntime.getImageId());

					serverWC.save(true, null);

					break;
				}
			}
		}
		catch (Exception e) {
			LiferayGradleCore.logError("Failed to add server and runtime", e);
		}
	}

	private void _cleanUpWorkspaceDockerServerAndRuntime() {
		String dockerContainerName = _projectInfo.getDockerContainerId();

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			IServer[] servers = ServerCore.getServers();

			if (ListUtil.isNotEmpty(servers)) {
				for (IServer server : servers) {
					PortalDockerServer portalDockerServer = (PortalDockerServer)server.loadAdapter(
						PortalDockerServer.class, null);

					if ((portalDockerServer == null) ||
						!dockerContainerName.equals(portalDockerServer.getContainerName())) {

						continue;
					}

					IRuntime runtime = server.getRuntime();

					server.delete();

					if (runtime != null) {
						runtime.delete();
					}
				}
			}
		}
		catch (Exception e) {
			if (e instanceof IOException) {
				LiferayGradleCore.logError("Failed to connect docker daemon", e);
			}
			else if (e instanceof CoreException) {
				LiferayGradleCore.logError("Failed to cleanup server and runtime", e);
			}
		}

		IDockerSupporter dockerSupporter = new LiferayGradleDockerSupporter();

		try {
			dockerSupporter.removeDockerContainer(new NullProgressMonitor());

			dockerSupporter.cleanDockerImage(new NullProgressMonitor());
		}
		catch (Exception e) {
			LiferayServerCore.logError("Failed to remove clean docker environment", e);
		}
	}

	private ProjectInfo _projectInfo;

}