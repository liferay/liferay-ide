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

package com.liferay.ide.eclipse.server.util;

import com.liferay.ide.eclipse.ui.util.LaunchHelper;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
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
public class PortalSupportHelper extends LaunchHelper {

	protected String[] rootLibs = new String[] {
		"portal-service.jar"
	};
	
	protected IPath libRoot;
	
	protected String[] portalLibs = new String[] {
		"portal-impl.jar", "spring-aop.jar"
	};
	
	protected IPath portalRoot;
	
	protected String[] userLibs;

	protected URL[] supportLibs;

	protected File outputFile;

	public PortalSupportHelper(
		IPath libRoot, IPath portalRoot, String portalSupportClass, File outputFile, File errorFile, URL[] supportLibs,
		String[] userLibs) {

		this(libRoot, portalRoot, portalSupportClass, outputFile, errorFile, supportLibs, userLibs, null);
	}

	public PortalSupportHelper(
		IPath libRoot, IPath portalRoot, String portalSupportClass, File outputFile, File errorFile, URL[] supportLibs,
		String[] userLibs, String extraArg) {
		
		super(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

		setLaunchSync(true);

		setLaunchInBackground(true);

		setLaunchCaptureInConsole(true);

		setLaunchIsPrivate(true);

		setMainClass(portalSupportClass);

		setOutputFile(outputFile);

		setLaunchArgs(new String[] {
			portalSupportClass, outputFile.getAbsolutePath(), errorFile.getAbsolutePath(), extraArg
		});

		setMode(ILaunchManager.RUN_MODE);

		this.libRoot = libRoot;

		this.portalRoot = portalRoot;

		this.userLibs = userLibs;
		
		this.supportLibs = supportLibs;

//		this.launchTimeout = 2000;
	}

	@Override
	public ILaunchConfigurationWorkingCopy createLaunchConfiguration()
		throws CoreException {
		
		ILaunchConfigurationWorkingCopy config = super.createLaunchConfiguration();

		// set default for common settings
		CommonTab tab = new CommonTab();
		tab.setDefaults(config);
		tab.dispose();

		if (outputFile != null && outputFile.getParentFile().exists()) {
			config.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_FILE, outputFile.getAbsolutePath());
		}
		
		return config;		
	}

	@Override
	protected void addUserEntries(ClasspathModel model)
		throws CoreException {
		
		if (supportLibs != null && supportLibs.length > 0) {
			for (URL supportLib : supportLibs) {
				model.addEntry(
					ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(supportLib.getPath())));
			}
		}
		
		for (String rootLib : rootLibs) {
			model.addEntry(ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry(libRoot.append(rootLib)));
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
		else {
			for (String jarFile : this.portalRoot.append("WEB-INF/lib").toFile().list()) {
				if (jarFile.endsWith(".jar")) {
					model.addEntry(
						ClasspathModel.USER,
						JavaRuntime.newArchiveRuntimeClasspathEntry(portalRoot.append("WEB-INF/lib").append(jarFile)));
				}
			}
		}
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

}
