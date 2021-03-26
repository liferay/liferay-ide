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

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.SocketUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class PortalDockerServerLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {

	public static final String ID = "com.liferay.ide.server.portal.launch";

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		IServer server = ServerUtil.getServer(config);

		if (server != null) {
			IRuntime runtime = server.getRuntime();

			if (runtime == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server runtime is invalid."));
			}

			PortalDockerRuntime portalRuntime = (PortalDockerRuntime)runtime.loadAdapter(
				PortalDockerRuntime.class, monitor);

			if (portalRuntime == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server portal runtime is invalid."));
			}

			IStatus status = portalRuntime.validate();

			if (status.getSeverity() == IStatus.ERROR) {
				throw new CoreException(status);
			}

			PortalDockerServer portalDockerServer = (PortalDockerServer)server.loadAdapter(
				PortalDockerServer.class, monitor);

			if (portalDockerServer == null) {
				throw new CoreException(LiferayServerCore.createErrorStatus("Server portal server is invalid."));
			}

			try {
				_launchServer(server, config, mode, launch, monitor);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startDebugLaunch(IServer server, ILaunchConfiguration config, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		// setup the run launch so we get console monitor

		String connectorId = getVMConnectorId(config);

		IVMConnector connector = null;

		if (connectorId == null) {
			connector = JavaRuntime.getDefaultVMConnector();
		}
		else {
			connector = JavaRuntime.getVMConnector(connectorId);
		}

		if (connector == null) {
			abort(
				"Debugging connector not specified.", null,
				IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE);
		}

		Map<String, String> connectMap = config.getAttribute(
			IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, new HashMap<String, String>());

		String hostname = server.getHost();
		String port = config.getAttribute("port", "8000");

		connectMap.put("hostname", hostname);
		connectMap.put("port", port);

		IPreferencesService preferencesService = Platform.getPreferencesService();

		int connectTimeout = preferencesService.getInt(
			LaunchingPlugin.ID_PLUGIN, JavaRuntime.PREF_CONNECT_TIMEOUT, JavaRuntime.DEF_CONNECT_TIMEOUT, null);

		connectMap.put("timeout", String.valueOf(connectTimeout));  //$NON-NLS-1$

		// check for cancellation

		if (monitor.isCanceled()) {
			return;
		}

		if (!launch.isTerminated()) {
			IStatus canConnect = SocketUtil.canConnect(hostname, port);

			if (canConnect.isOK()) {
				connector.connect(connectMap, monitor, launch);
			}
		}

		// check for cancellation

		if (monitor.isCanceled() || launch.isTerminated()) {
			IDebugTarget[] debugTargets = launch.getDebugTargets();

			for (IDebugTarget target : debugTargets) {
				if (target.canDisconnect()) {
					target.disconnect();
				}
			}

			return;
		}

		monitor.done();
	}

	private IProcess _createTerminateableStreamsProxyProcess(
		IServer server, PortalDockerServer portalServer, final PortalDockerServerBehavior poratlServerBehaviour,
		ILaunch launch, boolean debug, ILaunchConfiguration config, IProgressMonitor monitor) {

		try {
			if ((server == null) || (portalServer == null) || (poratlServerBehaviour == null) || (launch == null)) {
				return null;
			}

			IPortalDockerStreamsProxy streamsProxy = new PortalDockerServerLogStreamsProxy(
				portalServer, poratlServerBehaviour, launch);

			IProcess retvalProcess = poratlServerBehaviour.getProcess();

			if (retvalProcess == null) {
				retvalProcess = new PortalDockerServerMonitorProcess(
					server, poratlServerBehaviour, launch, debug, streamsProxy, config, this, monitor);

				launch.addProcess(retvalProcess);
			}
			else {
				launch.addProcess(retvalProcess);
			}

			return retvalProcess;
		}
		catch (Exception e) {
		}

		return null;
	}

	private void _launchServer(
			IServer server, ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		PortalDockerServer portalServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, monitor);

		PortalDockerServerBehavior portalServerBehavior = (PortalDockerServerBehavior)server.loadAdapter(
			PortalDockerServerBehavior.class, monitor);

		IProcess streamProxyProcess = _createTerminateableStreamsProxyProcess(
			server, portalServer, portalServerBehavior, launch, ILaunchManager.DEBUG_MODE.equals(mode), config,
			monitor);

		if (portalServerBehavior.getProcess() == null) {
			portalServerBehavior.setProcess(streamProxyProcess);
		}

		portalServerBehavior.launchServer(launch, mode, monitor);

		server.addServerListener(
			new IServerListener() {

				@Override
				public void serverChanged(ServerEvent event) {
					if ((event.getKind() & ServerEvent.MODULE_CHANGE) > 0) {
						AbstractSourceLookupDirector sourceLocator =
							(AbstractSourceLookupDirector)launch.getSourceLocator();

						try {
							String memento = config.getAttribute(
								ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String)null);

							if (memento != null) {
								sourceLocator.initializeFromMemento(memento);
							}
							else {
								sourceLocator.initializeDefaults(config);
							}
						}
						catch (CoreException ce) {
							LiferayServerCore.logError("Could not reinitialize source lookup director", ce);
						}
					}
					else if (((event.getKind() & ServerEvent.SERVER_CHANGE) > 0) &&
							 (event.getState() == IServer.STATE_STOPPED)) {

						server.removeServerListener(this);
					}
					else if (((event.getKind() & ServerEvent.SERVER_CHANGE) > 0) &&
							 (event.getState() == IServer.STATE_STOPPING)) {

						IJobManager jobManager = Job.getJobManager();

						Job[] jobs = jobManager.find(null);

						for (Job job : jobs) {
							if (job.getProperty(ILiferayServer.LIFERAY_SERVER_JOB) != null) {
								job.cancel();
							}
						}
					}
				}

			});

		try {
			portalServerBehavior.addProcessListener(launch.getProcesses()[0]);
		}
		catch (Exception e) {
			portalServerBehavior.cleanup();
		}
	}

}