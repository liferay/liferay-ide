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
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.HealthCheck;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class PortalDockerServerStateStartThread {

	// delay before pinging starts

	/**
	 * Create a new PingThread.
	 *
	 * @param server
	 * @param url
	 * @param maxPings  the maximum number of times to try pinging, or -1 to
	 *                  continue forever
	 * @param behaviour
	 */
	public PortalDockerServerStateStartThread(IServer server, PortalDockerServerBehavior behaviour) {
		_server = server;
		_behaviour = behaviour;
		_mointorProcess = behaviour.getProcess();

		int serverStartTimeout = server.getStartTimeout();

		if (serverStartTimeout < (_defaultTimeout / 1000)) {
			_timeout = _defaultTimeout;
		}
		else {
			_timeout = serverStartTimeout * 1000;
		}

		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			PortalDockerServer portalServer = behaviour.getPortalServer();

			InspectContainerCmd inspectContainerCmd = dockerClient.inspectContainerCmd(portalServer.getContainerId());

			InspectContainerResponse containerSetting = inspectContainerCmd.exec();

			ContainerConfig config = containerSetting.getConfig();

			HealthCheck healthCheck = config.getHealthcheck();

			List<String> healthCheckCommands = healthCheck.getTest();

			for (String commandContent : healthCheckCommands) {
				if (commandContent.contains("http")) {
					_liferayHomeUrl = new URL(
						commandContent.substring(commandContent.indexOf("\"") + 1, commandContent.lastIndexOf("\"")));

					break;
				}
			}
		}
		catch (Exception e) {
			try {
				_server.stop(true);
				_mointorProcess.terminate();

				_behaviour.triggerCleanupEvent(_mointorProcess);
			}
			catch (DebugException de) {
				LiferayServerCore.logError(de);
			}

			LiferayServerCore.logError(e);
		}

		Thread t = new Thread("Liferay Portal Docker Server Start Thread") {

			public void run() {
				_startedTime = System.currentTimeMillis();
				startMonitor();
			}

		};

		t.setDaemon(true);
		t.start();
	}

	// delay between pings

	/**
	 * Tell the pinging to stop.
	 */
	public void stop() {
		_stop = true;
	}

	/**
	 * Ping the server until it is started. Then set the server state to
	 * STATE_STARTED.
	 */
	protected void startMonitor() {
		long currentTime = 0;

		try {
			Thread.sleep(_pingDelay);
		}
		catch (Exception e) {
		}

		while (!_stop) {
			try {
				currentTime = System.currentTimeMillis();

				if ((currentTime - _startedTime) > _timeout) {
					try {
						_server.stop(true);
						_mointorProcess.terminate();

						_behaviour.triggerCleanupEvent(_mointorProcess);
					}
					catch (Exception e) {
					}

					_stop = true;

					break;
				}

				URLConnection conn = _liferayHomeUrl.openConnection();

				conn.setReadTimeout(_pingInterval);

				((HttpURLConnection)conn).setInstanceFollowRedirects(false);
				int code = ((HttpURLConnection)conn).getResponseCode();

				if (!_stop && (code != 404)) {
					Thread.sleep(200);
					_behaviour.setServerStarted();
					_stop = true;
				}

				Thread.sleep(1000);
			}
			catch (Exception e) {
				if (!_stop) {
					try {
						Thread.sleep(_pingInterval);
					}
					catch (InterruptedException ie) {
					}
				}
			}
		}
	}

	private static int _pingDelay = 2000;
	private static int _pingInterval = 250;

	private PortalDockerServerBehavior _behaviour;
	private long _defaultTimeout = 15 * 60 * 1000;
	private URL _liferayHomeUrl;
	private IProcess _mointorProcess;
	private IServer _server;
	private long _startedTime;
	private boolean _stop = false;
	private long _timeout = 0;

}