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
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import java.net.InetAddress;
import java.net.URI;

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