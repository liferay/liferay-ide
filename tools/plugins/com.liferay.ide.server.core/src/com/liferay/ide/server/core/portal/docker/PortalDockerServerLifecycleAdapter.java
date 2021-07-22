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

import com.github.dockerjava.api.model.Container;

import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.Objects;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public class PortalDockerServerLifecycleAdapter extends ServerLifecycleAdapter {

	@Override
	public void serverRemoved(IServer server) {
		PortalDockerServer portalDockerServer = (PortalDockerServer)server.loadAdapter(
			PortalDockerServer.class, new NullProgressMonitor());

		if (portalDockerServer == null) {
			return;
		}

		IDockerServer dockerServer = LiferayServerCore.getDockerServer();

		if (Objects.nonNull(dockerServer)) {
			try {
				Container dockerContainer = LiferayDockerClient.getDockerContainerByName(
					portalDockerServer.getContainerName());

				if (Objects.nonNull(LiferayWorkspaceUtil.getWorkspaceProject())) {
					if (Objects.nonNull(dockerContainer) &&
						Objects.equals(dockerContainer.getId(), portalDockerServer.getContainerId())) {

						dockerServer.removeDockerContainer(new NullProgressMonitor());
					}
				}
				else {
					LiferayDockerClient.removeDockerContainer(portalDockerServer.getContainerId());
				}
			}
			catch (Exception e) {
				LiferayServerCore.logError("Failed to remove server", e);
			}
		}
	}

}