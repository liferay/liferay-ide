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

package com.liferay.ide.eclipse.server.tomcat.core.job;

import com.liferay.ide.eclipse.sdk.job.SDKJob;
import com.liferay.ide.eclipse.server.tomcat.core.IPortalTomcatRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.PortalTomcatPlugin;
import com.liferay.ide.eclipse.server.tomcat.core.PortalTomcatServerBehavior;
import com.liferay.ide.eclipse.server.tomcat.core.util.PortalTomcatUtil;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class CleanAppServerJob extends SDKJob {

	public CleanAppServerJob(IProject project) {
		super("Clean App Server");

		setUser(true);

		setProject(project);
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

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus retval = Status.OK_STATUS;

		if (monitor != null) {
			monitor.beginTask("Running clean-app-server task...", IProgressMonitor.UNKNOWN);
		}

		try {
			IRuntime runtime = ServerUtil.getRuntime(project);
			IServer[] servers = ServerUtil.getServersForRuntime(runtime);

			for (IServer server : servers) {
				String mode = server.getServerState() == IServer.STATE_STARTED ? server.getMode() : null;

				if (mode != null) {
					PortalTomcatUtil.syncStopServer(server);
				}
			}

			IPortalTomcatRuntime portalTomcatRuntime = PortalTomcatUtil.getPortalTomcatRuntime(runtime);
			IPath bundleZipLocation = portalTomcatRuntime.getBundleZipLocation();

			IStatus status = getSDK().cleanAppServer(project, bundleZipLocation);

			assertStatus(status);

			for (IServer server : servers) {
				// need to mark all other server modules at needing republishing since ext will wipe out webapps folder
				IModule[] modules = server.getModules();

				for (IModule mod : modules) {
					IModule[] m = new IModule[] {
						mod
					};

					((PortalTomcatServerBehavior) server.getAdapter(PortalTomcatServerBehavior.class)).setModulePublishState2(
						m, IServer.PUBLISH_STATE_FULL);
				}
			}


		}
		catch (Exception ex) {
			retval = PortalTomcatPlugin.createErrorStatus(ex);
		}

		if (monitor != null) {
			monitor.done();
		}

		return retval;
	}

}
