/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.server.core.IPortalServer;

import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;

@SuppressWarnings("restriction")
public class PortalTomcatServer extends TomcatServer implements IPortalTomcatConstants, IPortalServer {
	
	public PortalTomcatServer() {
		super();
	}

	@Override
	public void importRuntimeConfiguration(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		if (runtime == null) {
			configuration = null;
			return;
		}
		IPath path = runtime.getLocation().append("conf");
		
		String id = getServer().getServerType().getId();
		IFolder folder = getServer().getServerConfiguration();
		if (id.indexOf("60") > 0) {
			configuration = new PortalTomcat60Configuration(folder);
		}

		if (path.toFile().exists()) {
			try {
				configuration.importFromPath(path, isTestEnvironment(), monitor);
			}
			catch (CoreException ce) {
				// ignore
				configuration = null;
				throw ce;
			}
		}
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		super.setDefaults(monitor);
		setTestEnvironment(false);
		setDeployDirectory(IPortalTomcatConstants.DEFAULT_DEPLOYDIR);
		setSaveSeparateContextFiles(true);
	}

	@Override
	public TomcatConfiguration getTomcatConfiguration() throws CoreException {
		if (configuration == null) {
			IFolder folder = getServer().getServerConfiguration();
			if (folder == null || !folder.exists()) {
				String path = null;
				if (folder != null) {
					path = folder.getFullPath().toOSString();
					IProject project = folder.getProject();
					if (project != null && project.exists() && !project.isOpen()) 
						throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorConfigurationProjectClosed, path, project.getName()), null));
				}
				throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorNoConfiguration, path), null));
			}
			
			String id = getServer().getServerType().getId();
			if (id.indexOf("60") > 0) {
				configuration = new PortalTomcat60Configuration(folder);
			}
			try {
				((IPortalTomcatConfiguration)configuration).load(folder, null);
			} catch (CoreException ce) {
				// ignore
				configuration = null;
				throw ce;
			}
		}
		return configuration;
	}

	public String getAutoDeployDirectory() {
		return getAttribute(IPortalTomcatConstants.PROPERTY_AUTO_DEPLOY_DIR, "../deploy");
	}
	
	public void setAutoDeployDirectory(String dir) {
		setAttribute(IPortalTomcatConstants.PROPERTY_AUTO_DEPLOY_DIR, dir);
	}

	public IPortalTomcatConfiguration getPortalTomcatConfiguration() throws CoreException {
		return (IPortalTomcatConfiguration)getTomcatConfiguration();
	}

	public String getAutoDeployInterval() {
		return getAttribute(IPortalTomcatConstants.PROPERTY_AUTO_DEPLOY_INTERVAL, IPortalTomcatConstants.DEFAULT_AUTO_DEPLOY_INTERVAL);
	}
	
	public void setAutoDeployInterval(String interval) {
		setAttribute(IPortalTomcatConstants.PROPERTY_AUTO_DEPLOY_INTERVAL, interval);
	}

	@Override
	public ITomcatVersionHandler getTomcatVersionHandler() {
		ITomcatVersionHandler handler = super.getTomcatVersionHandler();
		if (handler instanceof PortalTomcat60Handler) {
			((PortalTomcat60Handler)handler).setCurrentServer(getServer());
		}
		return handler;
	}

	
	public URL getPortalHomeUrl() {
		try {
			TomcatConfiguration config = getTomcatConfiguration();
			if (config == null)
				return null;
			
			String url = "http://" + getServer().getHost();
			int port = config.getMainPort().getPort();
			port = ServerUtil.getMonitoredPort(getServer(), port, "web");
			if (port != 80)
				url += ":" + port;
			return new URL(url);
		} catch (Exception ex) {
			return null;
		}
	}
}
