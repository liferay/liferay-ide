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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.server.util.SocketUtil;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class PortalDockerServerMonitorProcess implements IProcess {

	public PortalDockerServerMonitorProcess(
		IServer server, final PortalDockerServerBehavior serverBehavior, ILaunch launch, boolean debug,
		IPortalDockerStreamsProxy proxy, ILaunchConfiguration config, PortalDockerServerLaunchConfigDelegate delegate,
		IProgressMonitor monitor) {

		_server = server;
		_dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);
		_streamsProxy = proxy;
		_launch = launch;
		_debug = debug;
		_config = config;
		_delegate = delegate;

		startContainer(monitor);
	}

	public boolean canTerminate() {
		return !_streamsProxy.isTerminated();
	}

	/**
	 * Fires a terminate event.
	 */
	public void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	public <T> T getAdapter(Class<T> adapterType) {
		return (T)null;
	}

	public String getAttribute(String key) {
		return (String)this._attributerMap.get(key);
	}

	public int getExitValue() throws DebugException {
		return 0;
	}

	public String getLabel() {
		if (_label == null) {
			String host = null;
			String port = null;

			if (_server != null) {
				host = _server.getHost();
			}

			PortalDockerServer portalServer = (PortalDockerServer)_server.loadAdapter(PortalDockerServer.class, null);

			if (portalServer != null) {
				port = "8080";
			}

			_label = ((host != null) ? host : "") + ":" + ((port != null) ? port : "");
		}

		return _label;
	}

	public ILaunch getLaunch() {
		return _launch;
	}

	public IStreamsProxy getStreamsProxy() {
		return _streamsProxy;
	}

	public boolean isTerminated() {
		return LiferayDockerClient.verifyDockerContainerTerminated(_dockerServer.getContainerId());
	}

	public void setAttribute(String key, String value) {
		_attributerMap.put(key, value);
	}

	public void setStreamsProxy(IPortalDockerStreamsProxy streamsProxy) {
		_streamsProxy = streamsProxy;
	}

	public void terminate() throws DebugException {
		try {
			IStreamsProxy streamsProxy = getStreamsProxy();

			if (streamsProxy instanceof IPortalDockerStreamsProxy) {
				IPortalDockerStreamsProxy portalDockerStreamsProxy = (IPortalDockerStreamsProxy)streamsProxy;

				portalDockerStreamsProxy.terminate();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void fireCreateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager = DebugPlugin.getDefault();

		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[] {event});
		}
	}

	protected void startContainer(IProgressMonitor monitor) {
		if (_dockerServer != null) {
			if (_dockerServer.getGogoShellPort() == null) {
				LiferayServerCore.logError("The Gogo shell port you configured in Dockerfile.ext is in used.");

				return;
			}

			try {
				IDockerSupporter dockerSupporter = LiferayServerCore.getDockerSupporter();

				if (dockerSupporter == null) {
					LiferayServerCore.logError("Failed to get docker supporter");

					return;
				}

				dockerSupporter.startDockerContainer(monitor);

				fireCreateEvent();

				if (_debug) {
					try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
						InspectContainerCmd inspectContainerCmd = dockerClient.inspectContainerCmd(
							_dockerServer.getContainerId());

						InspectContainerResponse inspectContainerCmdResponse = inspectContainerCmd.exec();

						ContainerConfig config = inspectContainerCmdResponse.getConfig();

						String[] containerEnv = config.getEnv();

						boolean enableDebug = Stream.of(
							containerEnv
						).filter(
							env -> env.contains("LIFERAY_JPDA_ENABLED=true")
						).findAny(
						).isPresent();

						if (enableDebug) {
							Thread checkDebugThread = new Thread("Liferay Portal Docker Server Debug Checking Thread") {

								public void run() {
									try {
										boolean debugPortStarted = false;

										Stream.of(
											containerEnv
										).filter(
											env -> env.contains("JPDA_ADDRESS")
										).findFirst(
										).ifPresent(
											value -> {
												try {
													ILaunchConfigurationWorkingCopy workingCopy =
														_config.getWorkingCopy();

													workingCopy.setAttribute("port", value.split(":")[1]);

													_config = workingCopy.doSave();
												}
												catch (CoreException e) {
													e.printStackTrace();
												}
											}
										);

										do {
											IStatus canConnect = SocketUtil.canConnect(
												_server.getHost(), _config.getAttribute("port", "8000"));

											try {
												if (canConnect.isOK()) {
													_delegate.startDebugLaunch(_server, _config, _launch, monitor);

													if (ListUtil.isNotEmpty(_launch.getDebugTargets())) {
														debugPortStarted = true;
													}
												}

												sleep(500);
											}
											catch (Exception e) {
											}
										}
										while (!debugPortStarted);
									}
									catch (Exception e) {
									}
								}

							};

							checkDebugThread.setPriority(1);
							checkDebugThread.setDaemon(true);
							checkDebugThread.start();

							try {
								checkDebugThread.join(Integer.MAX_VALUE);

								if (checkDebugThread.isAlive()) {
									checkDebugThread.interrupt();

									throw new TimeoutException();
								}
							}
							catch (InterruptedException | TimeoutException e) {
								e.printStackTrace();
							}
						}
					}
					catch (IOException e) {
						LiferayServerCore.logError("Failed to connect docker daemon", e);
					}
				}
			}
			catch (Exception e) {
				fireTerminateEvent();
				LiferayServerCore.logError("Failed to start docker container", e);
			}
		}
	}

	private Map<String, String> _attributerMap = new HashMap<>();
	private ILaunchConfiguration _config;
	private boolean _debug;
	private PortalDockerServerLaunchConfigDelegate _delegate;
	private PortalDockerServer _dockerServer;
	private String _label;
	private ILaunch _launch;
	private IServer _server;
	private IPortalDockerStreamsProxy _streamsProxy;

}