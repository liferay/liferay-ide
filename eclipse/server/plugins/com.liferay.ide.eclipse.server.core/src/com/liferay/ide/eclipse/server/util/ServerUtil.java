/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.util;

import com.liferay.ide.eclipse.server.core.IPortalConstants;
import com.liferay.ide.eclipse.server.core.IPortalRuntime;
import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ServerUtil {

	protected static final IStatus emptyInstallDirStatus = createErrorStatus("Install directory is empty.");

	protected static final IStatus installDirDoesNotExist = createErrorStatus("Install directory does not exist.");

	protected static final IStatus invalidInstallDirStatus = createErrorStatus("Invalid installation directory.");

	/*
	 * public static ServerInstallSpec verifyInstallationPath(IPath path, String
	 * serverId) { ServerInstallSpec spec = new ServerInstallSpec(); if (path ==
	 * null) { spec.setStatus(emptyInstallDirStatus); return spec; } String dir
	 * = path.toOSString(); if (dir.trim().length() == 0) {
	 * spec.setStatus(emptyInstallDirStatus); return spec; } File file = new
	 * File(dir); if (!file.exists()) { spec.setStatus(installDirDoesNotExist);
	 * return spec; } if (!dir.endsWith(File.separator)) { dir +=
	 * File.separator; } // need to figure out if we have a valid bundle
	 * ServerInstallSpec[] serverSpecs =
	 * PortalServerCorePlugin.readServerInstallSpecs(); for (ServerInstallSpec
	 * serverSpec : serverSpecs) { IStatus result =
	 * serverSpec.checkInstallationDir(dir); if (result != null &&
	 * result.isOK()) { return serverSpec; } }
	 * spec.setStatus(invalidInstallDirStatus); return spec; }
	 */

	// public static PortalRuntime getPortalRuntime(IRuntime runtime) {
	// return runtime != null ? (PortalRuntime)
	// runtime.loadAdapter(PortalRuntime.class, null) : null;
	// }

	public static URL checkForLatestInstallableRuntime(String id) {

		try {
			URL url = new URL(IPortalConstants.INSTALLABLE_UPDATE_URL);

			InputStream is = url.openStream();

			Properties props = new Properties();

			props.load(is);

			String installableUrl = props.getProperty(id);

			return new URL(installableUrl);
		}
		catch (Exception e) {
			// best effort
		}

		return null;
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PortalServerCorePlugin.PLUGIN_ID, msg);
	}

	public static IPath getPortalRoot(IJavaProject project) {
		return getPortalRoot(project.getProject());
	}

	public static IPath getPortalRoot(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime = facetedProject.getPrimaryRuntime();

			if (runtime != null) {
				return ServerUtil.getPortalRoot(runtime);
			}
		}
		catch (CoreException e) {
			PortalServerCorePlugin.logError(e);
		}

		return null;
	}

	public static IPath getPortalRoot(org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetRuntime) {
		IPortalRuntime runtime = (IPortalRuntime) getRuntimeAdapter(facetRuntime, IPortalRuntime.class);

		return runtime != null ? runtime.getRoot() : null;
	}

	public static IPortalRuntime getPortalRuntime(IProject project)
		throws CoreException {

		return (IPortalRuntime) getRuntimeAdapter(
			ProjectFacetsManager.create(project).getPrimaryRuntime(), IPortalRuntime.class);
	}

	public static IPortalRuntime getPortalRuntime(IRuntime runtime) {
		if (runtime != null) {
			return (IPortalRuntime) runtime.createWorkingCopy().loadAdapter(IPortalRuntime.class, null);
		}

		return null;
	}

	public static IPortalRuntime getPortalRuntime(IServer server) {
		if (server != null) {
			return getPortalRuntime(server.getRuntime());
		}

		return null;
	}

	public static IRuntime getRuntime(IProject project)
		throws CoreException {

		return (IRuntime) getRuntimeAdapter(ProjectFacetsManager.create(project).getPrimaryRuntime(), IRuntime.class);
	}

	public static IRuntime getRuntime(org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime) {
		return ServerCore.findRuntime(runtime.getProperty("id"));
	}

	public static IRuntimeWorkingCopy getRuntime(String runtimeTypeId, IPath location) {
		IRuntimeType runtimeType = ServerCore.findRuntimeType(runtimeTypeId);

		try {
			IRuntime runtime = runtimeType.createRuntime("runtime", null);

			IRuntimeWorkingCopy runtimeWC = runtime.createWorkingCopy();
			runtimeWC.setName("Runtime");
			runtimeWC.setLocation(location);

			return runtimeWC;
		}
		catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Object getRuntimeAdapter(
		org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetRuntime, Class<?> adapterClass) {

		String runtimeId = facetRuntime.getProperty("id");

		for (org.eclipse.wst.server.core.IRuntime runtime : ServerCore.getRuntimes()) {
			if (runtime.getId().equals(runtimeId)) {

				if (IRuntime.class.equals(adapterClass)) {
					return runtime;
				}

				IRuntimeWorkingCopy runtimeWC = null;

				if (!runtime.isWorkingCopy()) {
					runtimeWC = runtime.createWorkingCopy();
				}
				else {
					runtimeWC = (IRuntimeWorkingCopy) runtime;
				}

				return (IPortalRuntime) runtimeWC.loadAdapter(adapterClass, null);
			}
		}

		return null;
	}

	public static IServerWorkingCopy getServerForRuntime(IRuntime runtime) {
		for (IServerType serverType : ServerCore.getServerTypes()) {
			if (serverType.getRuntimeType().equals(runtime.getRuntimeType())) {
				try {
					return serverType.createServer("server", null, runtime, null);
				}
				catch (CoreException e) {
				}
			}
		}

		return null;
	}

	public static boolean isPortalRuntime(BridgedRuntime bridgedRuntime) {
		IRuntime runtime = ServerCore.findRuntime(bridgedRuntime.getProperty("id"));

		return isPortalRuntime(runtime);
	}

	public static boolean isPortalRuntime(IRuntime runtime) {
		return getPortalRuntime(runtime) != null;
	}

	public static boolean isPortalRuntime(IServer server) {
		return getPortalRuntime(server) != null;
	}
}
