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

package com.liferay.ide.eclipse.server.tomcat.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntimeClasspathProvider;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayTomcatRuntimeClasspathProvider extends TomcatRuntimeClasspathProvider {

	public LiferayTomcatRuntimeClasspathProvider() {
	}

	protected IClasspathEntry[] resolveClasspathContainerForPath(IPath installPath, String runtimeId) {
		List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
		
		if ( runtimeId.indexOf( "60" ) > 0 || installPath.append( "lib" ).toFile().exists() ) {
			IPath path = installPath.append("lib");
			
			addLibraryEntries(list, path.toFile(), true);
		}

		// go through all classpath entries and remove some unneeded ones
		List<IClasspathEntry> optimizedList = new ArrayList<IClasspathEntry>();
		
		List<String> excludes = Arrays.asList(ILiferayTomcatConstants.LIB_EXCLUDES);
		
		for (IClasspathEntry entry : list) {
			if (!excludes.contains(entry.getPath().lastSegment())) {
				optimizedList.add(entry);
			}
		}

		return (IClasspathEntry[]) optimizedList.toArray(new IClasspathEntry[optimizedList.size()]);
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer(final IProject project, final IRuntime runtime) {
		IPath installPath = runtime.getLocation();

		if (installPath == null)
			return new IClasspathEntry[0];

		String runtimeId = runtime.getRuntimeType().getId();

		return resolveClasspathContainerForPath(installPath, runtimeId);

	}


	protected void updateClasspath(IProject project, IRuntime runtime) {
		// IJavaProject javaProject = JavaCore.create(project);
	}

}
