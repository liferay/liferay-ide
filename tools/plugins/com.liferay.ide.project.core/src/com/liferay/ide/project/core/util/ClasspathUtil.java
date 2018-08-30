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

package com.liferay.ide.project.core.util;

import com.liferay.ide.project.core.SDKClasspathContainer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Simon Jiang
 */
public class ClasspathUtil {

	public static boolean hasNewLiferaySDKContainer(IClasspathEntry[] entries) {
		boolean retVal = false;

		for (IClasspathEntry entry : entries) {
			IPath path = entry.getPath();

			String segment = path.segment(0);

			if ((entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) && segment.equals(SDKClasspathContainer.ID)) {
				retVal = true;

				break;
			}
		}

		return retVal;
	}

	public static boolean isPluginContainerEntry(IClasspathEntry e) {
		IPath path = e.getPath();

		String segment = path.segment(0);

		if ((e != null) && (e.getEntryKind() == IClasspathEntry.CPE_CONTAINER) &&
			segment.equals(SDKClasspathContainer.ID)) {

			return true;
		}

		return false;
	}

	public static void updateRequestContainer(IProject project) throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);
		IPath containerPath = null;

		IClasspathEntry[] entries = javaProject.getRawClasspath();

		for (IClasspathEntry entry : entries) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IPath path = entry.getPath();

				String segment = path.segment(0);

				if (segment.equals(SDKClasspathContainer.ID)) {
					containerPath = entry.getPath();

					break;
				}
			}
		}

		if (containerPath != null) {
			IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(containerPath, javaProject);

			String id = containerPath.segment(0);

			if (id.equals(SDKClasspathContainer.ID)) {
				ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(id);

				initializer.requestClasspathContainerUpdate(containerPath, javaProject, classpathContainer);
			}
		}
	}

}