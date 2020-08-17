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

package com.liferay.ide.project.core.library;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.SDKClasspathContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.jst.j2ee.classpathdep.IClasspathDependencyConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;

/**
 * @author Gregory Amerson
 */
public class SDKClasspathContainerInstallOperation extends LibraryProviderOperation {

	public SDKClasspathContainerInstallOperation() {
	}

	@Override
	public void execute(LibraryProviderOperationConfig config, IProgressMonitor monitor) throws CoreException {
		IFacetedProjectBase facetedProject = config.getFacetedProject();

		IJavaProject javaProject = JavaCore.create(facetedProject.getProject());

		IPath containerPath = getClasspathContainerPath();

		// IDE-413 check to make sure that the containerPath doesn't already existing.

		IClasspathEntry[] entries = javaProject.getRawClasspath();

		for (IClasspathEntry entry : entries) {
			if (containerPath.equals(entry.getPath())) {
				return;
			}
		}

		IAccessRule[] accessRules = {};

		IClasspathAttribute[] attributes = {
			JavaCore.newClasspathAttribute(
				IClasspathDependencyConstants.CLASSPATH_COMPONENT_NON_DEPENDENCY, StringPool.EMPTY)
		};

		IClasspathEntry newEntry = JavaCore.newContainerEntry(containerPath, accessRules, attributes, false);

		IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];

		System.arraycopy(entries, 0, newEntries, 0, entries.length);

		newEntries[entries.length] = newEntry;

		javaProject.setRawClasspath(newEntries, monitor);
	}

	public IPath getClasspathContainerPath() {
		return new Path(SDKClasspathContainer.ID);
	}

}