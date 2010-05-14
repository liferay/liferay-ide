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

import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;
import com.liferay.ide.eclipse.ui.util.LaunchHelper;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.internal.debug.ui.classpath.ClasspathModel;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortalClassHelper extends LaunchHelper {

	private String[] defaultLibs = new String[] {
		"portal-service.jar"
	};
	
	private IPath libRoot;
	
	private String[] portalLibs = new String[] {
		"portal-impl.jar", "spring-aop.jar"
	};
	
	private IPath portalRoot;
	
	private String[] userLibs;

	public PortalClassHelper(
		IPath libRoot, IPath portalRoot, String portalSupportClass, String[] args, String[] userLibs) {
		
		super(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		setLaunchSync(true);
		setLaunchInBackground(true);
		setLaunchCaptureInConsole(true);
		setLaunchIsPrivate(true);
		setMainClass(portalSupportClass);
		setLaunchArgs(args);
		setMode(ILaunchManager.RUN_MODE);
		this.libRoot = libRoot;
		this.portalRoot = portalRoot;
		this.userLibs = userLibs;
	}

	@Override
	public ILaunchConfigurationWorkingCopy createLaunchConfiguration()
		throws CoreException {
		
		ILaunchConfigurationWorkingCopy config = super.createLaunchConfiguration();
		// set default for common settings
		CommonTab tab = new CommonTab();
		tab.setDefaults(config);
		tab.dispose();
		config.setAttribute(
			IDebugUIConstants.ATTR_CAPTURE_IN_FILE, PortalServerCorePlugin.getDefault().getStateLocation().append(
				"portletclasshelper.log").toOSString());
		
		return config;		
	}

	@Override
	protected void addUserEntries(ClasspathModel model)
		throws CoreException {
		
		URL url = PortalServerCorePlugin.getDefault().getBundle().getEntry("portal-support/portal-support.jar");
		
		try {
			URL fileUrl = FileLocator.toFileURL(url);
			
			IPath portalSupportPath = new Path(fileUrl.getPath());
			
			model.addEntry(ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry(portalSupportPath));			
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		for (String defaultLib : defaultLibs) {
			model.addEntry(ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry(libRoot.append(defaultLib)));
		}

		for (String portalLib : portalLibs) {
			model.addEntry(ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry(portalRoot.append(
				"WEB-INF/lib").append(portalLib)));
		}

		if (userLibs != null) {
			for (String userLib : userLibs) {
				model.addEntry(ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry(portalRoot.append(
					"WEB-INF/lib").append(userLib)));
			}
		}
	}

}
