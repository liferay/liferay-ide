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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class PortalDockerServerBehavior
	extends PortalServerBehavior implements IJavaLaunchConfigurationConstants, ILiferayServerBehavior {

	public static final String ATTR_STOP = "stop-server";

	public PortalDockerServerBehavior() {
		_watchProjects = new LinkedHashSet<>();
	}

	@Override
	public void addProcessListener(IProcess newProcess) {
		if ((_processListener != null) || (_process == null)) {
			return;
		}

		_processListener = new IDebugEventSetListener() {

			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					for (DebugEvent event : events) {
						if ((_process != null) && _process.equals(event.getSource()) &&
							(event.getKind() == DebugEvent.TERMINATE)) {

							cleanup();
						}
					}
				}
			}

		};

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.addDebugEventListener(_processListener);
	}

	public void cleanup() {
		if (startedThread != null) {
			startedThread.stop();

			startedThread = null;
		}

		if (stopedThread != null) {
			stopedThread.stop();

			stopedThread = null;
		}

		if ((restartThread != null) && (isServerRestarting() == false)) {
			restartThread.stop();

			restartThread = null;
		}

		setProcess(null);

		if (_processListener != null) {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();

			debugPlugin.removeDebugEventListener(_processListener);

			_processListener = null;
		}

		setServerState(IServer.STATE_STOPPED);

		_watchProjects.clear();
	}

	@Override
	public void dispose() {
		if (_process != null) {
			setProcess(null);
		}
	}

	public PortalDockerRuntime getPortalRuntime() {
		PortalDockerRuntime retval = null;

		IServer s = getServer();

		IRuntime runtime = s.getRuntime();

		if (runtime != null) {
			retval = (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
		}

		return retval;
	}

	public PortalDockerServer getPortalServer() {
		PortalDockerServer retval = null;

		IServer s = getServer();

		if (s != null) {
			retval = (PortalDockerServer)s.loadAdapter(PortalDockerServer.class, null);
		}

		return retval;
	}

	public IProcess getProcess() {
		return _process;
	}

	public boolean isServerRestarting() {
		return serverRestarting;
	}

	public void launchServer(ILaunch launch, String mode, IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();

		if (Objects.equals(launchConfiguration.getAttribute(ATTR_STOP, "false"), "true")) {
			return;
		}

		IStatus status = getPortalRuntime().validate();

		if ((status != null) && (status.getSeverity() == IStatus.ERROR)) {
			throw new CoreException(status);
		}

		setServerRestartState(false);
		setServerState(IServer.STATE_STARTING);
		setMode(mode);

		try {
			startedThread = new PortalDockerServerStateStartThread(getServer(), this);

			Job job = new Job("Logs the Docker container") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IDockerSupporter dockerSupporter = LiferayServerCore.getDockerSupporter();

					dockerSupporter.logDockerContainer(new NullProgressMonitor());

					return Status.OK_STATUS;
				}

			};

			job.setProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB, new Object());

			job.schedule();
		}
		catch (Exception e) {
			LiferayServerCore.logError("Can not ping for portal startup.");
		}
	}

	@Override
	public void restart(final String launchMode) throws CoreException {
		setServerRestarting(true);
		getServer().stop(
			false,
			new IServer.IOperationListener() {

				@Override
				public void done(IStatus result) {
					try {
						restartThread = new PortalDockerServeStateRestartThread(
							getServer(), PortalDockerServerBehavior.this, launchMode);
					}
					catch (Exception e) {
						LiferayServerCore.logError("Can not restart Blade Server.");
					}
				}

			});
	}

	public void setServerRestarting(boolean serverRestarting) {
		this.serverRestarting = serverRestarting;
	}

	public void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	public void setServerStoped() {
		setServerState(IServer.STATE_STOPPED);
	}

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
		throws CoreException {

		super.setupLaunchConfiguration(workingCopy, monitor);

		workingCopy.setAttribute("hostname", getServer().getHost());

		int port = SocketUtil.findFreePort();

		if (port != -1) {
			workingCopy.setAttribute("port", port);
		}
	}

	@Override
	public void stop(boolean force) {
		if (force) {
			try {
				_executStopCommand();

				if (stopedThread == null) {
					stopedThread = new PortalDockerServerStateStopThread(getServer(), this);
				}

				terminate();
				setServerState(IServer.STATE_STOPPED);
			}
			catch (Exception e) {
			}

			return;
		}

		int state = getServer().getServerState();

		// If stopped or stopping, no need to run stop command again

		if ((state == IServer.STATE_STOPPED) || (state == IServer.STATE_STOPPING)) {
			return;
		}
		else if (state == IServer.STATE_STARTING) {
			try {
				_executStopCommand();

				if (stopedThread == null) {
					stopedThread = new PortalDockerServerStateStopThread(getServer(), this);
				}

				terminate();
				setServerState(IServer.STATE_STOPPED);
			}
			catch (Exception e) {
			}

			return;
		}

		try {
			if (state != IServer.STATE_STOPPED) {
				setServerState(IServer.STATE_STOPPING);
			}

			if (stopedThread == null) {
				stopedThread = new PortalDockerServerStateStopThread(getServer(), this);
			}

			final ILaunch launch = getServer().getLaunch();

			if (launch != null) {
				terminate();
			}

			_executStopCommand();

			setServerState(IServer.STATE_STOPPED);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Can not ping for portal startup.");
		}
	}

	public void triggerCleanupEvent(Object eventSource) {
		DebugEvent event = new DebugEvent(eventSource, DebugEvent.TERMINATE);

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.fireDebugEventSet(new DebugEvent[] {event});
	}

	protected static String renderCommandLine(String[] commandLine, String separator) {
		if ((commandLine == null) || (commandLine.length < 1)) {
			return "";
		}

		StringBuffer buf = new StringBuffer(commandLine[0]);

		for (int i = 1; i < commandLine.length; i++) {
			buf.append(separator);
			buf.append(commandLine[i]);
		}

		return buf.toString();
	}

	protected void setProcess(IProcess newProcess) {
		if ((_process != null) && !_process.isTerminated()) {
			try {
				_process.terminate();
			}
			catch (Exception e) {
				LiferayServerCore.logError(e);
			}
		}

		_process = newProcess;
	}

	protected void terminate() {
		if (getServer().getServerState() == IServer.STATE_STOPPED) {
			return;
		}

		try {
			setServerState(IServer.STATE_STOPPING);

			ILaunch launch = getServer().getLaunch();

			if (launch != null) {
				launch.terminate();
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError("Error killing the process", e);
		}
	}

	protected transient PortalDockerServeStateRestartThread restartThread = null;
	protected boolean serverRestarting = false;
	protected transient PortalDockerServerStateStartThread startedThread = null;
	protected transient PortalDockerServerStateStopThread stopedThread = null;

	private void _executStopCommand() throws Exception {
		try {
			IDockerSupporter dockerSupporter = LiferayServerCore.getDockerSupporter();

			dockerSupporter.stopDockerContainer(null);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Failed to stop docker server", e);
		}
	}

	private transient IProcess _process;
	private transient IDebugEventSetListener _processListener;
	private Set<IProject> _watchProjects;

}