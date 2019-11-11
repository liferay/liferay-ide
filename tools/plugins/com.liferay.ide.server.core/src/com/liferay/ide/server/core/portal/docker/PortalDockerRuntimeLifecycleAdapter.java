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
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.List;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.util.RuntimeLifecycleAdapter;

/**
 * @author Simon Jiang
 */
public class PortalDockerRuntimeLifecycleAdapter extends RuntimeLifecycleAdapter {

	@Override
	public void runtimeRemoved(IRuntime runtime) {
		PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);

		if (dockerRuntime == null) {
			return;
		}

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withImageNameFilter(dockerRuntime.getImageTag());
			listImagesCmd.withShowAll(true);

			List<Image> images = listImagesCmd.exec();

			if (ListUtil.isNotEmpty(images)) {
				ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();

				ListContainersCmd showContainerCmd = listContainersCmd.withShowAll(true);

				List<Container> containers = showContainerCmd.exec();

				String imageId = dockerRuntime.getImageId();

				for (Container container : containers) {
					if (imageId.equals(container.getImageId())) {
						RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(container.getId());

						removeContainerCmd.exec();
					}
				}

				RemoveImageCmd removeImageCmd = dockerClient.removeImageCmd(dockerRuntime.getImageId());

				removeImageCmd.exec();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}
	}

}