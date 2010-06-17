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

package com.liferay.ide.eclipse.server.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author Greg Amerson
 */
public abstract class PluginClasspathContainer implements IClasspathContainer {

	private IPath path;	
	
	private IPath portalRoot;
	
	private IJavaProject project;

	public PluginClasspathContainer(IPath containerPath, IJavaProject project, IPath portalRoot) {
		this.path = containerPath;
		
		this.project = project;
		
		this.portalRoot = portalRoot;
	}

	public IClasspathEntry[] getClasspathEntries() {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		
		for (String pluginJar : getPluginJars()) {
			entries.add(JavaCore.newLibraryEntry(				
				this.portalRoot.append("/WEB-INF/lib/" + pluginJar),
				
				PortalServerCorePlugin.getDefault().getPortalSourcePath(new Path(pluginJar).removeFileExtension()),
					null, new IAccessRule[] {}, new IClasspathAttribute[] {}, false));
		}

		for (String pluginPackageJar : getPluginPackageJars()) {
			IPath entryPath = this.portalRoot.append("/WEB-INF/lib/" + pluginPackageJar);
			
			entries.add(JavaCore.newLibraryEntry(
				entryPath, PortalServerCorePlugin.getDefault().getPortalSourcePath(
					new Path(pluginPackageJar).removeFileExtension()), null, new IAccessRule[] {},					
					new IClasspathAttribute[] {}, false));
		}

		return entries.toArray(new IClasspathEntry[entries.size()]);		
	}

	public abstract String getDescription();

	public int getKind() {
		return K_APPLICATION;
	}

	public IPath getPath() {
		return this.path;
	}

	protected abstract String[] getPluginJars();

	protected String[] getPluginPackageJars() {
		String[] jars = new String[0];
		
		IVirtualComponent comp = ComponentCore.createComponent(this.project.getProject());
		
		if (comp != null) {			
			IFolder webroot = (IFolder) comp.getRootFolder().getUnderlyingFolder();
			
			IFile pluginPackageFile = webroot.getFile("WEB-INF/liferay-plugin-package.properties");
			
			if (pluginPackageFile.exists()) {
				Properties props = new Properties();
				
				try {
					props.load(pluginPackageFile.getContents());
					
					String deps = props.getProperty("portal-dependency-jars", "");
					
					String[] split = deps.split(",");
					
					if (split.length > 0 && !(CoreUtil.isNullOrEmpty(split[0]))) {
						jars = split;						
					}
				}				
				catch (Exception e) {
				}
			}
		}
		
		return jars;		
	}

}
