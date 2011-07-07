/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
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

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, LiferayServerCorePlugin.PLUGIN_ID, msg);
	}

	public static IServerWorkingCopy createServerForRuntime(IRuntime runtime) {
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

	public static IPath getAppServerDir(org.eclipse.wst.common.project.facet.core.runtime.IRuntime serverRuntime) {
		ILiferayRuntime runtime = (ILiferayRuntime) getRuntimeAdapter(serverRuntime, ILiferayRuntime.class);

		return runtime != null ? runtime.getAppServerDir() : null;
	}

	public static IPath getPortalDir(IJavaProject project) {
		return getPortalDir(project.getProject());
	}

	public static IPath getPortalDir(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime = facetedProject.getPrimaryRuntime();

			if (runtime != null) {
				return ServerUtil.getPortalDir(runtime);
			}
		}
		catch (CoreException e) {
			LiferayServerCorePlugin.logError(e);
		}

		return null;
	}

	public static IPath getPortalDir(org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetRuntime) {
		ILiferayRuntime runtime = (ILiferayRuntime) getRuntimeAdapter(facetRuntime, ILiferayRuntime.class);

		return runtime != null ? runtime.getPortalDir() : null;
	}

	public static ILiferayRuntime getLiferayRuntime(IProject project)
		throws CoreException {

		return (ILiferayRuntime) getRuntimeAdapter(
			ProjectFacetsManager.create(project).getPrimaryRuntime(), ILiferayRuntime.class);
	}

	public static ILiferayRuntime getLiferayRuntime(IRuntime runtime) {
		if (runtime != null) {
			return (ILiferayRuntime) runtime.createWorkingCopy().loadAdapter(ILiferayRuntime.class, null);
		}

		return null;
	}

	public static ILiferayRuntime getLiferayRuntime(IServer server) {
		if (server != null) {
			return getLiferayRuntime(server.getRuntime());
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

				return (ILiferayRuntime) runtimeWC.loadAdapter(adapterClass, null);
			}
		}

		return null;
	}

	public static IServer[] getServersForRuntime(IRuntime runtime) {
		List<IServer> serverList = new ArrayList<IServer>();

		if (runtime != null) {
			IServer[] servers = ServerCore.getServers();

			if (!CoreUtil.isNullOrEmpty(servers)) {
				for (IServer server : servers) {
					if (runtime.equals(server.getRuntime())) {
						serverList.add(server);
					}
				}
			}
		}

		return serverList.toArray(new IServer[0]);
	}

	public static boolean isExistingVMName(String name) {
		for (IVMInstall vm : JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE).getVMInstalls()) {
			if (vm.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLiferayRuntime(BridgedRuntime bridgedRuntime) {
		IRuntime runtime = ServerCore.findRuntime(bridgedRuntime.getProperty("id"));

		return isLiferayRuntime(runtime);
	}

	public static boolean isLiferayRuntime(IRuntime runtime) {
		return getLiferayRuntime(runtime) != null;
	}

	public static boolean isLiferayRuntime(IServer server) {
		return getLiferayRuntime(server) != null;
	}

	public static boolean isValidPropertiesFile(File file) {
		if (file == null || !file.exists()) {
			return false;
		}

		try {
			new PropertiesConfiguration(file);
		}
		catch (Exception e) {
			return false;
		}

		return true;

	}

	public static boolean isLiferayProject( IProject project ) {
		boolean retval = false;

		if ( project == null ) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create( project );

			if ( facetedProject != null ) {
				for ( IProjectFacetVersion facet : facetedProject.getProjectFacets() ) {
					IProjectFacet projectFacet = facet.getProjectFacet();

					if ( projectFacet.getId().startsWith( "liferay" ) ) {
						retval = true;
						break;
					}
				}
			}
		}
		catch ( Exception e ) {
		}

		return retval;
	}

	public static boolean isExtProject( IProject project ) {
		return hasFacet( project, ProjectFacetsManager.getProjectFacet( "liferay.ext" ) );
	}

	public static boolean hasFacet( IProject project, IProjectFacet checkProjectFacet ) {
		boolean retval = false;
		if ( project == null || checkProjectFacet == null ) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create( project );
			if ( facetedProject != null && checkProjectFacet != null ) {
				for ( IProjectFacetVersion facet : facetedProject.getProjectFacets() ) {
					IProjectFacet projectFacet = facet.getProjectFacet();
					if ( checkProjectFacet.equals( projectFacet ) ) {
						retval = true;
						break;
					}
				}
			}
		}
		catch ( CoreException e ) {
		}
		return retval;
	}

	public static void terminateLaunchesForConfig( ILaunchConfigurationWorkingCopy config ) throws DebugException {
		ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();

		for ( ILaunch launch : launches ) {
			if ( launch.getLaunchConfiguration().equals( config ) ) {
				launch.terminate();
			}
		}
	}
}
