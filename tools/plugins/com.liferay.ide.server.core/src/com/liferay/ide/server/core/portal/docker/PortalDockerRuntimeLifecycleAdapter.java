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
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.util.RuntimeLifecycleAdapter;

/**
 * @author Simon Jiang
 * @author Ethan Sun
 */
public class PortalDockerRuntimeLifecycleAdapter extends RuntimeLifecycleAdapter {

	@Override
	public void runtimeRemoved(IRuntime runtime) {
		PortalDockerRuntime dockerRuntime = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);

		if (dockerRuntime == null) {
			return;
		}

		String imageRepoTag = String.join(":", dockerRuntime.getImageRepo(), dockerRuntime.getImageTag());

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withShowAll(false);

			List<Image> images = listImagesCmd.exec();

			Stream<Image> imageStream = images.stream();

			imageStream.filter(
				image -> {
					String imageRepoTagDocker = image.getRepoTags()[0];

					return imageRepoTagDocker.equals(imageRepoTag);
				}
			).findFirst(
			).ifPresent(
				image -> {
					IDockerSupporter dockerSupporter = LiferayServerCore.getDockerSupporter();

					dockerSupporter.removeDockerContainer(new NullProgressMonitor());
				}
			);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Failed to remove runtime", e);
		}
	}

}