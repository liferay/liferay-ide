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

package com.liferay.ide.eclipse.project.core.library;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
 * @author Greg Amerson
 */
public abstract class PluginLibraryInstallOperation extends LibraryProviderOperation {

	public PluginLibraryInstallOperation() {
		super();
	}

	@Override
	public void execute(LibraryProviderOperationConfig config, IProgressMonitor monitor)
		throws CoreException {
		
		IFacetedProjectBase facetedProject = config.getFacetedProject();
		
		IProject project = facetedProject.getProject();
		
		IJavaProject javaProject = JavaCore.create(project);
		
		IPath container = createClasspathContainerPath();

		IAccessRule[] accessRules = new IAccessRule[] {};

		IClasspathAttribute[] attributes = new IClasspathAttribute[] {
			JavaCore.newClasspathAttribute(IClasspathDependencyConstants.CLASSPATH_COMPONENT_NON_DEPENDENCY, "")
		};

		IClasspathEntry entry = JavaCore.newContainerEntry(container, accessRules, attributes, false);
		
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		
		IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
		
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		
		newEntries[entries.length] = entry;
		
		javaProject.setRawClasspath(newEntries, monitor);
	}

	protected abstract IPath createClasspathContainerPath();

}
