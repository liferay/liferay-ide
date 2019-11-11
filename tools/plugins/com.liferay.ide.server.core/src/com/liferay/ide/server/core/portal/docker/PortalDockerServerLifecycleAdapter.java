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

package com.liferay.ide.server.core.portal.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.Container;

import com.google.common.collect.Lists;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.List;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;

/**
 * @author Simon Jiang
 */
public class PortalDockerServerLifecycleAdapter extends ServerLifecycleAdapter {

	@Override
	public void serverRemoved(IServer server) {
		PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);

		if (dockerServer == null) {
			return;
		}

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();

			listContainersCmd.withNameFilter(Lists.newArrayList(dockerServer.getContainerName()));
			listContainersCmd.withShowAll(true);
			listContainersCmd.withLimit(1);

			List<Container> conatiners = listContainersCmd.exec();

			if (ListUtil.isNotEmpty(conatiners)) {
				RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(dockerServer.getContainerId());

				removeContainerCmd.exec();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}
	}

}