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

package com.liferay.ide.server.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

import com.google.common.collect.Lists;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import java.net.InetAddress;
import java.net.URI;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang.SystemUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Simon Jiang
 */
public class LiferayDockerClient {

	public static DockerClient getDockerClient() throws CoreException {
		DefaultDockerClientConfig.Builder createDefaultConfigBuilder =
			DefaultDockerClientConfig.createDefaultConfigBuilder();

		createDefaultConfigBuilder.withDockerHost(_getDefaultDockerUrl());

		createDefaultConfigBuilder.withRegistryUrl("https://registry.hub.docker.com/v2/repositories/liferay/portal");
		createDefaultConfigBuilder.withRegistryUrl("https://registry.hub.docker.com/v2/repositories/liferay/dxp");

		String dockerCertPath = System.getenv("DOCKER_CERT_PATH");

		if (CoreUtil.isNotNullOrEmpty(dockerCertPath)) {
			createDefaultConfigBuilder.withDockerCertPath(dockerCertPath);
		}

		DefaultDockerClientConfig config = createDefaultConfigBuilder.build();

		if (SystemUtils.IS_OS_WINDOWS) {
			URI dockerHostUri = config.getDockerHost();

			IStatus canConnectStatus = SocketUtil.canConnect(
				dockerHostUri.getHost(), String.valueOf(dockerHostUri.getPort()));

			if (!canConnectStatus.isOK()) {
				throw new CoreException(
					LiferayServerCore.createErrorStatus("Can not connect to docker api. Please check configuration"));
			}
		}

		NettyDockerCmdExecFactory cmdFactory = new NettyDockerCmdExecFactory();

		DockerClientBuilder dockerClientBuilder = DockerClientBuilder.getInstance(config);

		dockerClientBuilder.withDockerCmdExecFactory(cmdFactory);

		return dockerClientBuilder.build();
	}

	public static Container getDockerContainerByName(String dockerContainerId) {
		try (DockerClient dockerClient = getDockerClient()) {
			ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();

			listContainersCmd.withNameFilter(Lists.newArrayList(dockerContainerId));
			listContainersCmd.withLimit(1);

			List<Container> containers = listContainersCmd.exec();

			if (ListUtil.isEmpty(containers)) {
				return null;
			}

			return containers.get(0);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Failed to create docker runtime", e);
		}

		return null;
	}

	public static Image getDockerImageByName(String dockerImageRepo) {
		try (DockerClient dockerClient = getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withShowAll(true);
			listImagesCmd.withDanglingFilter(false);

			listImagesCmd.withImageNameFilter(dockerImageRepo);

			List<Image> imagetList = listImagesCmd.exec();

			Stream<Image> imageStream = imagetList.stream();

			Optional<Image> dockerImage = imageStream.filter(
				image -> {
					String imageRepoTagDocker = image.getRepoTags()[0];

					return imageRepoTagDocker.equals(dockerImageRepo);
				}
			).findFirst();

			if (dockerImage.isPresent()) {
				return dockerImage.get();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError("Failed to create docker runtime", e);
		}

		return null;
	}

	public static String getDockerImageId(String dockerImageRepo) {
		Image dockerImage = getDockerImageByName(dockerImageRepo);

		if (Objects.nonNull(dockerImage)) {
			return dockerImage.getId();
		}

		return null;
	}

	public static boolean verifyDockerContainer(String dockerContainerId) {
		return false;
	}

	public static boolean verifyDockerContainerTerminated(String dockerContainerId) {
		try (DockerClient dockerClient = getDockerClient()) {
			InspectContainerCmd inspectContainerCmd = dockerClient.inspectContainerCmd(dockerContainerId);

			InspectContainerResponse response = inspectContainerCmd.exec();

			InspectContainerResponse.ContainerState containerState = response.getState();

			return !containerState.getRunning();
		}
		catch (Exception execption) {
			LiferayServerCore.logError(execption);
		}

		return false;
	}

	public static boolean verifyDockerImageByName(String dockerImageRepo) {
		try (DockerClient dockerClient = getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withShowAll(true);
			listImagesCmd.withDanglingFilter(false);

			listImagesCmd.withImageNameFilter(dockerImageRepo);

			List<Image> imagetList = listImagesCmd.exec();

			Stream<Image> imageStream = imagetList.stream();

			Optional<Image> dockerImage = imageStream.filter(
				image -> {
					String imageRepoTagDocker = image.getRepoTags()[0];

					return imageRepoTagDocker.equals(dockerImageRepo);
				}
			).findFirst();

			return dockerImage.isPresent();
		}
		catch (Exception e) {
			LiferayServerCore.logError("Failed to create docker runtime", e);
		}

		return false;
	}

	private static String _getDefaultDockerUrl() {
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

}