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

package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat60Handler;
import org.eclipse.jst.server.tomcat.core.internal.TomcatVersionHelper;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.xml.sax.SAXException;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortalTomcat60Handler extends Tomcat60Handler {

	private IServer currentServer;

	@Override
	public IStatus canAddModule(IModule module) {
		// check to make sure that the user isn't trying to add multiple
		// ext-plugins to server
		if (module != null && currentServer != null) {
			if (ProjectUtil.isExtProject(module.getProject())) {
				for (IModule currentModule : currentServer.getModules()) {
					if (ProjectUtil.isExtProject(currentModule.getProject())) {
						return PortalTomcatPlugin.createErrorStatus("Portal can only have on Ext-plugin deployed at a time.");
					}
				}
			}
		}
		
		return super.canAddModule(module);
	}

	@Override
	public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, IPath deployPath, boolean isTestEnv) {
		List<String> runtimeVMArgs = new ArrayList<String>();

		runtimeVMArgs.add("-Xmx1024m");		
		runtimeVMArgs.add("-XX:MaxPermSize=256m");
		runtimeVMArgs.add("-Dfile.encoding=UTF8");
		runtimeVMArgs.add("-Duser.timezone=GMT");
		runtimeVMArgs.add("-Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false");
		runtimeVMArgs.add("-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager");
		runtimeVMArgs.add("-Djava.security.auth.login.config=\"" + configPath.toOSString() + "/conf/jaas.config\"");
		runtimeVMArgs.add("-Djava.util.logging.config.file=\"" + installPath.toOSString() +
			"/conf/logging.properties\"");
		runtimeVMArgs.add("-Djava.io.tmpdir=\"" + installPath.toOSString() + "/temp\"");

		try {
			ensurePortalIDEPropertiesExists(installPath, configPath);
			
			runtimeVMArgs.add("-Dexternal-properties=portal-ide.properties");
		}
		catch (Exception e) {
			PortalTomcatPlugin.logError(e);
		}

		Collections.addAll(runtimeVMArgs, super.getRuntimeVMArguments(installPath, configPath, deployPath, isTestEnv));
		
		return runtimeVMArgs.toArray(new String[runtimeVMArgs.size()]);
	}

	public void setCurrentServer(IServer server) {
		this.currentServer = server;
	}

	protected void ensurePortalIDEPropertiesExists(IPath installPath, IPath configPath)
		throws FileNotFoundException, IOException {
		
		IPath idePropertiesPath = installPath.append("../portal-ide.properties");
		
		String hostName = "localhost";
		
		try {
			ServerInstance server =
				TomcatVersionHelper.getCatalinaServerInstance(configPath.append("conf/server.xml"), null, null);
			
			hostName = server.getHost().getName();
		}
		catch (SAXException e) {
			PortalTomcatPlugin.logError(e);
		}

		// read portal-developer.properties
		// Properties devProps = new Properties();
		// IPath devPropertiesPath =
		// installPath.append("webapps/ROOT/WEB-INF/classes/portal-developer.properties");
		// if (devPropertiesPath.toFile().exists()) {
		// devProps.load(new FileReader(devPropertiesPath.toFile()));
		// }

		// if (idePropertiesPath.toFile().exists()) {
		// String value =
		// CoreUtil.readPropertyFileValue(idePropertiesPath.toFile(),
		// "auto.deploy.tomcat.conf.dir");
		// if (configPath.append("conf/Catalina/"+hostName).toFile().equals(new
		// File(value))) {
		// return;
		// }
		// }
		
		Properties props = new Properties();
		
		props.put("include-and-override", "portal-developer.properties");
		
		props.put("auto.deploy.tomcat.conf.dir", configPath.append("conf/Catalina/" + hostName).toOSString());

		if (this.currentServer != null) {
			PortalTomcatServer server =
				(PortalTomcatServer) this.currentServer.loadAdapter(PortalTomcatServer.class, null);
			
			if (server != null) {
				IPath runtimLocation = server.getTomcatRuntime().getRuntime().getLocation();
				
				String autoDeployDir = server.getAutoDeployDirectory();
				
				if (!IPortalTomcatConstants.DEFAULT_AUTO_DEPLOYDIR.equals(autoDeployDir)) {
					IPath autoDeployDirPath = new Path(autoDeployDir);
					
					if (autoDeployDirPath.isAbsolute() && autoDeployDirPath.toFile().exists()) {
						props.put("auto.deploy.deploy.dir", server.getAutoDeployDirectory());
					}
					else {
						File autoDeployDirFile = new File(runtimLocation.toFile(), autoDeployDir);
						
						if (autoDeployDirFile.exists()) {
							props.put("auto.deploy.deploy.dir", autoDeployDirFile.getPath());
						}
					}
				}

				props.put("auto.deploy.interval", server.getAutoDeployInterval());
			}
		}
		
		props.store(new FileOutputStream(idePropertiesPath.toFile()), null);
	}

}
