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

package com.liferay.ide.server.tomcat.core.job;

import com.liferay.ide.sdk.core.SDKJob;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.server.tomcat.core.LiferayTomcatServerBehavior;
import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class CleanAppServerJob extends SDKJob {

	public CleanAppServerJob(IProject project, String bundleZipLocation) {
		super("Clean App Server");

		setUser(true);

		setProject(project);

		_bundleZipLocation = bundleZipLocation;
	}

	protected void assertStatus(IStatus status) throws CoreException {
		if (status == null) {
			throw new CoreException(LiferayTomcatPlugin.createErrorStatus("null status"));
		}

		if (!status.isOK()) {
			throw new CoreException(status);
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus retval = Status.OK_STATUS;

		if (monitor != null) {
			monitor.beginTask(Msgs.runningCleanAppServerTask, IProgressMonitor.UNKNOWN);
		}

		try {
			PortalBundle portalBundle = ServerUtil.getPortalBundle(project);

			IPath appServerPath = portalBundle.getAppServerDir();

			String appServerDir = appServerPath.toOSString();

			IServer[] servers = ServerCore.getServers();

			for (IServer server : servers) {
				String mode = server.getServerState() == IServer.STATE_STARTED ? server.getMode() : null;

				if (mode != null) {
					IRuntime serverRuntime = server.getRuntime();

					IPath serverLocation = serverRuntime.getLocation();

					if (appServerDir.equals(serverLocation.toOSString())) {
						LiferayTomcatUtil.syncStopServer(server);
					}
				}
			}

			IStatus status = getSDK().cleanAppServer(project, _bundleZipLocation, appServerDir, monitor);

			assertStatus(status);

			for (IServer server : servers) {

				// need to mark all other server modules at needing republishing since ext will wipe out webapps folder

				IModule[] modules = server.getModules();

				for (IModule mod : modules) {
					IModule[] m = {mod};

					((LiferayTomcatServerBehavior)server.loadAdapter(LiferayTomcatServerBehavior.class, monitor)).
						setModulePublishState2(m, IServer.PUBLISH_STATE_FULL);
				}
			}
		}
		catch (Exception ex) {
			retval = LiferayTomcatPlugin.createErrorStatus(ex);
		}

		if (monitor != null) {
			monitor.done();
		}

		return retval;
	}

	private String _bundleZipLocation;

	private static class Msgs extends NLS {

		public static String runningCleanAppServerTask;

		static {
			initializeMessages(CleanAppServerJob.class.getName(), Msgs.class);
		}

	}

}