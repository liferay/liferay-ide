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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.util.SocketUtil;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Gregory Amerson
 * @author Tao Tao
 */
@SuppressWarnings("restriction")
public class RemoteServer extends ServerDelegate implements IRemoteServerWorkingCopy {

	public static final String ATTR_REMOTE_SERVER_MODULE_IDS_LIST = "remote-server-module-ids-list";

	public RemoteServer() {
	}

	public boolean canMakeHttpConnection() {
		String host = getServer().getHost();
		String http = getHTTPPort();

		Socket socket = new Socket();

		new Thread() {

			@Override
			public void run() {
				try {
					sleep(3000);
					socket.close();
				}
				catch (Exception e) {
				}
			}

		}.start();

		IStatus status = SocketUtil.canConnect(socket, host, http);

		if ((status != null) && status.isOK()) {
			return true;
		}
		else {
			Socket socket1 = new Socket();

			status = SocketUtil.canConnectProxy(socket1, host, http);

			if ((status != null) && status.isOK()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {

		// check to see if we can add this to remote server

		if (ListUtil.isNotEmpty(add)) {
			for (IModule addModule : add) {
				IProject addModuleProject = addModule.getProject();

				if (!CoreUtil.isLiferayProject(addModuleProject)) {
					return LiferayServerCore.error(Msgs.notAddNonLiferayPluginProjectModule);
				}

				IStatus facetStatus = FacetUtil.verifyFacets(addModuleProject, getServer());

				if ((facetStatus != null) && !facetStatus.isOK()) {
					return facetStatus;
				}

				// make sure that EXT is disabled for now for remote deployment

				if (ServerUtil.isExtProject(addModuleProject)) {
					return LiferayServerCore.error(Msgs.extPluginDeployment);
				}
			}
		}
		else if (ListUtil.isNotEmpty(remove)) {
			for (IModule removeModule : remove) {
				IProject removeModuleProject = removeModule.getProject();

				if (removeModuleProject == null) {

					// something has happened to the project lets just remove it

					return Status.OK_STATUS;
				}

				IStatus facetStatus = FacetUtil.verifyFacets(removeModuleProject, getServer());

				if ((facetStatus != null) && !facetStatus.isOK()) {
					return facetStatus;
				}
			}
		}

		// ignore remove we can always try to remove;

		return Status.OK_STATUS;
	}

	public boolean getAdjustDeploymentTimestamp() {
		return getAttribute(ATTR_ADJUST_DEPLOYMENT_TIMESTAMP, DEFAULT_ADJUST_DEPLOYMENT_TIMESTAMP);
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		if (ListUtil.isEmpty(module)) {
			return null;
		}

		List<IModule> childModules = new ArrayList<>();

		for (IModule m : module) {
			IProject project = m.getProject();

			if (CoreUtil.isLiferayProject(project)) {
				IWebModule webModule = (IWebModule)m.loadAdapter(IWebModule.class, null);

				if (webModule != null) {
					IModule[] webModules = webModule.getModules();

					if (ListUtil.isNotEmpty(webModules)) {
						for (IModule childWebModule : webModules) {
							childModules.add(childWebModule);
						}
					}
				}
			}
		}

		return childModules.toArray(new IModule[0]);
	}

	public String getHost() {
		return getServer().getHost();
	}

	public String getHttpPort() {
		return getHTTPPort();
	}

	public String getHTTPPort() {
		return getAttribute(ATTR_HTTP_PORT, DEFAULT_HTTP_PORT);
	}

	public String getId() {
		return getServer().getId();
	}

	public String getLiferayPortalContextPath() {
		return getAttribute(ATTR_LIFERAY_PORTAL_CONTEXT_PATH, DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH);
	}

	public URL getModuleRootURL(IModule module) {
		return getPortalHomeUrl();
	}

	public String getPassword() {
		return getAttribute(ATTR_PASSWORD, DEFAULT_PASSWORD);
	}

	public URL getPluginContextURL(String context) {
		String httpPort = getHTTPPort();

		if (httpPort != null) {
			try {
				return new URL("http://" + getServer().getHost() + ":" + httpPort + context);
			}
			catch (MalformedURLException murle) {
			}
		}

		return null;
	}

	public URL getPortalHomeUrl() {
		String httpPort = getHTTPPort();
		String contextUrl = getLiferayPortalContextPath();

		if ((httpPort != null) && !CoreUtil.isNullOrEmpty(contextUrl)) {
			try {
				return new URL("http://" + getServer().getHost() + ":" + httpPort + contextUrl);
			}
			catch (MalformedURLException murle) {
			}
		}

		return null;
	}

	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {
		IModule[] modules = {module};

		IStatus status = canModifyModules(modules, null);

		if ((status == null) || !status.isOK()) {
			throw new CoreException(status);
		}

		return modules;
	}

	public String getServerManagerContextPath() {
		return getAttribute(ATTR_SERVER_MANAGER_CONTEXT_PATH, DEFAULT_SERVER_MANAGER_CONTEXT_PATH);
	}

	public String getUsername() {
		return getAttribute(ATTR_USERNAME, DEFAULT_USERNAME);
	}

	public URL getWebServicesListURL() {
		try {
			return new URL("http://" + getServer().getHost() + ":" + getHTTPPort() + "/tunnel-web/axis");
		}
		catch (MalformedURLException murle) {
			LiferayServerCore.logError("Unable to get web services list URL", murle);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		List<String> moduleIds = (List<String>)getAttribute(getModuleListAttr(), (List<String>)null);

		if (ListUtil.isNotEmpty(add)) {
			if (moduleIds == null) {
				moduleIds = new ArrayList<>(add.length);
			}

			for (IModule addModule : add) {
				if (moduleIds.contains(addModule.getId()) == false) {
					moduleIds.add(addModule.getId());
				}
			}
		}

		if (ListUtil.isNotEmpty(remove) && ListUtil.isNotEmpty(moduleIds)) {
			for (IModule removeModule : remove) {
				moduleIds.remove(removeModule.getId());
			}
		}

		if (moduleIds != null) {
			setAttribute(getModuleListAttr(), moduleIds);
		}
	}

	public void setAdjustDeploymentTimestamp(boolean adjustTimestamp) {
		setAttribute(ATTR_ADJUST_DEPLOYMENT_TIMESTAMP, adjustTimestamp);
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		super.setDefaults(monitor);

		String baseName = "Remote Liferay Server at " + getServer().getHost();

		String defaultName = baseName;

		int collision = 1;

		while (ServerPlugin.isNameInUse(getServer(), defaultName)) {
			defaultName = baseName + " (" + (collision++) + ")";
		}

		getServerWorkingCopy().setName(defaultName);
	}

	public void setHTTPPort(String httpPort) {
		setAttribute(ATTR_HTTP_PORT, httpPort);
	}

	public void setLiferayPortalContextPath(String path) {
		setAttribute(ATTR_LIFERAY_PORTAL_CONTEXT_PATH, path);
	}

	public void setPassword(String pw) {
		setAttribute(ATTR_PASSWORD, pw);
	}

	public void setServerManagerContextPath(String path) {
		setAttribute(ATTR_SERVER_MANAGER_CONTEXT_PATH, path);
	}

	public void setUsername(String username) {
		setAttribute(ATTR_USERNAME, username);
	}

	public IStatus validate(IProgressMonitor monitor) {
		IStatus status = null;

		try {
			String host = getServerWorkingCopy().getHost();

			if (monitor != null) {
				monitor.beginTask(NLS.bind(Msgs.validatingConnection, host, getHTTPPort()), IProgressMonitor.UNKNOWN);
			}

			if (!canMakeHttpConnection()) {
				return LiferayServerCore.createWarningStatus(NLS.bind(Msgs.serverNotAvailable, host, getHTTPPort()));
			}

			IServerManagerConnection connection = LiferayServerCore.getRemoteConnection(this);

			IStatus createErrorStatus = LiferayServerCore.error(_CONNECT_ERROR_MSG);

			status = connection.isAlive() ? Status.OK_STATUS : createErrorStatus;

			if (status.isOK()) {
				URL url = getPortalHomeUrl();

				if (url == null) {
					status = createErrorStatus;
				}
			}

			if (monitor != null) {
				monitor.done();
			}
		}
		catch (Exception e) {
			status = LiferayServerCore.createErrorStatus(e);
		}

		return status;
	}

	protected String getModuleListAttr() {
		return ATTR_REMOTE_SERVER_MODULE_IDS_LIST;
	}

	private static final String _CONNECT_ERROR_MSG = Msgs.notConnectRemoteIDEConnectorPlugin;

	private static class Msgs extends NLS {

		public static String extPluginDeployment;
		public static String notAddNonLiferayPluginProjectModule;
		public static String notConnectRemoteIDEConnectorPlugin;
		public static String serverNotAvailable;
		public static String validatingConnection;

		static {
			initializeMessages(RemoteServer.class.getName(), Msgs.class);
		}

	}

}