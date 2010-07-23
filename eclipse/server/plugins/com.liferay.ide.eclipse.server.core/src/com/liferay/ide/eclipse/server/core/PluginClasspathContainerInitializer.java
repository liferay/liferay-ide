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

import com.liferay.ide.eclipse.server.util.ServerUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Greg Amersont
 */
public class PluginClasspathContainerInitializer extends ClasspathContainerInitializer {

	public static final String ID = "com.liferay.ide.eclipse.server.plugin.container";

	@Override
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return true;
	}

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
		throws CoreException {
		
		IClasspathContainer classpathContainer = null;

		int count = containerPath.segmentCount();
		
		if (count != 2) {
			throw new CoreException(
				PortalServerCorePlugin.createErrorStatus("Invalid plugin classpath container should expecting 2 segments."));
		}

		String root = containerPath.segment(0);		
		
		if (!ID.equals(root)) {
			throw new CoreException(
				PortalServerCorePlugin.createErrorStatus("Invalid plugin classpath container, expecting container root " +
					ID));
		}

		String finalSegment = containerPath.segment(1);

		IPath portalRoot = ServerUtil.getPortalRoot(project);

		if (portalRoot == null) {
			return;
		}

		if (PortletClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			classpathContainer = new PortletClasspathContainer(containerPath, project, portalRoot);
		}
		else if (HookClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			classpathContainer = new HookClasspathContainer(containerPath, project, portalRoot);
		}
		else if (ExtClasspathContainer.SEGMENT_PATH.equals(finalSegment)) {
			classpathContainer = new ExtClasspathContainer(containerPath, project, portalRoot);
		}
		else {
			throw new CoreException(PortalServerCorePlugin.createErrorStatus("Invalid final segment of type: " +
				finalSegment));
		}

		JavaCore.setClasspathContainer(containerPath, 
			new IJavaProject[] {
				project
			}, new IClasspathContainer[] {
				classpathContainer
			}, null);
	}

	@Override
	public void requestClasspathContainerUpdate(
		IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion)
		throws CoreException {
		
		JavaCore.setClasspathContainer(containerPath, new IJavaProject[] {
			project
		}, new IClasspathContainer[] {
			containerSuggestion
		}, null);
	}

}
