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

import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntimeClasspathProvider;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortalTomcatRuntimeClasspathProvider extends TomcatRuntimeClasspathProvider {

	public PortalTomcatRuntimeClasspathProvider() {
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer(final IProject project, final IRuntime runtime) {
		IPath installPath = runtime.getLocation();

		if (installPath == null)
			return new IClasspathEntry[0];

		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		
		String runtimeId = runtime.getRuntimeType().getId();
		
		if (runtimeId.indexOf("60") > 0) {
			IPath path = installPath.append("lib");
			
			addLibraryEntries(list, path.toFile(), true);
		}

		// go through all classpath entries and remove some unneeded ones
		List<IClasspathEntry> optimizedList = new ArrayList<IClasspathEntry>();
		
		List<String> excludes = Arrays.asList(IPortalTomcatConstants.LIB_EXCLUDES);
		
		for (IClasspathEntry entry : list) {
			if (!excludes.contains(entry.getPath().lastSegment())) {
				optimizedList.add(entry);
			}
		}

		List<IClasspathEntry> sourceList = new ArrayList<IClasspathEntry>();

		for (IClasspathEntry entry : optimizedList) {
			if (shouldAttachSource(entry)) {
				IClasspathEntry newEntry =
					JavaCore.newLibraryEntry(entry.getPath(), PortalServerCorePlugin.getDefault().getPortalSourcePath(
						new Path(entry.getPath().removeFileExtension().lastSegment())), null);
				
				sourceList.add(newEntry);
			}
			else {
				sourceList.add(entry);
			}
		}

		try {
			project.getWorkspace().run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor)				
					throws CoreException {
					
					requestClasspathContainerUpdate(runtime, new IClasspathEntry[] {});
				}
			}, null);
		}
		catch (CoreException e) {
		}

		return (IClasspathEntry[]) sourceList.toArray(new IClasspathEntry[sourceList.size()]);
	}

	private boolean shouldAttachSource(IClasspathEntry entry) {
		// TODO reenable this IDE-83
		// if (entry != null &&
		// entry.getPath().lastSegment().matches("portal-.*\\.jar")) {
		// return true;
		// }
		
		return false;
	}

	protected void updateClasspath(IProject project, IRuntime runtime) {
		// IJavaProject javaProject = JavaCore.create(project);
	}

}
