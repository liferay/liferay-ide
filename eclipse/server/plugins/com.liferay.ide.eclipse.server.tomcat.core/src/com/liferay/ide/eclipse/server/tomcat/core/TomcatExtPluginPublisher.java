/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.core.AbstractPluginPublisher;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class TomcatExtPluginPublisher extends AbstractPluginPublisher {

	public TomcatExtPluginPublisher() {
		super();
	}

	public TomcatExtPluginPublisher(String facetId) {
		super(facetId);
	}

	public IStatus canPublishModule(IServer server, IModule module) {
		// check to make sure that the user isn't trying to add multiple
		// ext-plugins to server
		if (IPortalTomcatConstants.PREVENT_MULTI_EXT_PLUGINS_DEPLOY && module != null && server != null) {
			if (ProjectUtil.isExtProject(module.getProject())) {
				for (IModule currentModule : server.getModules()) {
					if (ProjectUtil.isExtProject(currentModule.getProject())) {
						return PortalTomcatPlugin.createErrorStatus("Portal can only have on Ext-plugin deployed at a time.");
					}
				}
			}
		}

		return Status.OK_STATUS;
	}

	public boolean prePublishModule(
		ServerBehaviourDelegate delegate, int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) {

		boolean publish = false;

		if (kind == IServer.PUBLISH_CLEAN || moduleTree == null) {
			return publish;
		}

		try {
			if (deltaKind == ServerBehaviourDelegate.ADDED || deltaKind == ServerBehaviourDelegate.CHANGED) {
				addExtModule(delegate, moduleTree[0], monitor);
			}
			else if (deltaKind == ServerBehaviourDelegate.REMOVED) {
				removeExtModule(moduleTree[0]);
			}
		}
		catch (Exception e) {
			PortalTomcatPlugin.logError("Failed pre-publishing ext module.", e);
		}

		return publish;
	}

	protected void addExtModule(ServerBehaviourDelegate delegate, IModule module, IProgressMonitor monitor)
		throws CoreException {

		SDK sdk = null;
		IProject project = module.getProject();

		sdk = ProjectUtil.getSDK(project, IPluginFacetConstants.LIFERAY_EXT_FACET);

		if (sdk == null) {
			throw new CoreException(
				PortalTomcatPlugin.createErrorStatus("No SDK for project configured. Could not deploy ext module"));
		}

		String mode =
			delegate.getServer().getServerState() == IServer.STATE_STARTED ? delegate.getServer().getMode() : null;

		if (mode != null) {
			stopServer(delegate);
		}

		IRuntime runtime = ServerUtil.getRuntime(project);

		String appServerDir = runtime.getLocation().toOSString();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("app.server.type", "tomcat");
		properties.put("app.server.dir", appServerDir);
		properties.put("app.server.deploy.dir", appServerDir + "/webapps");
		properties.put("app.server.lib.global.dir", appServerDir + "/lib/ext");
		properties.put("app.server.portal.dir", appServerDir + "/webapps/ROOT");

		IStatus status = sdk.directDeploy(project, properties);

		assertStatus(status);

		if (mode != null) {
			delegate.getServer().start(mode, monitor);
		}
	}

	protected void assertStatus(IStatus status)
		throws CoreException {

		if (status == null) {
			throw new CoreException(PortalTomcatPlugin.createErrorStatus("null status"));
		}

		if (!status.isOK()) {
			throw new CoreException(status);
		}
	}

	protected void removeExtModule(IModule module) {

		// TODO try to uninstall ext files
	}

	protected void stopServer(final ServerBehaviourDelegate delegate) {
		if (delegate.getServer().getServerState() != IServer.STATE_STARTED) {
			return;
		}

		Thread shutdownThread = new Thread() {

			@Override
			public void run() {
				delegate.stop(false);
				synchronized (delegate) {
					try {
						delegate.wait(5000);
					}
					catch (InterruptedException e) {
					}

					if (delegate.getServer().getServerState() != IServer.STATE_STOPPED) {
						delegate.stop(true);
					}
				}
			}

		};

		IServerListener shutdownListener = new IServerListener() {

			public void serverChanged(ServerEvent event) {
				if (event.getState() == IServer.STATE_STOPPED) {
					synchronized (delegate) {
						delegate.notifyAll();
					}
				}
			}
		};

		delegate.getServer().addServerListener(shutdownListener);

		try {
			shutdownThread.start();
			shutdownThread.join();
		}
		catch (InterruptedException e) {
		}

		delegate.getServer().removeServerListener(shutdownListener);
	}
}
