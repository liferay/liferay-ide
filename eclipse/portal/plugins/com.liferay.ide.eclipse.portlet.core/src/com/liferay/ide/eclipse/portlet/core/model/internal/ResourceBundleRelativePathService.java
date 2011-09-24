/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.RelativePathService;

import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class ResourceBundleRelativePathService extends RelativePathService {

	public static final String RB_FILE_EXTENSION = "properties";
	final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();
	final IWorkspaceRoot WORKSPACE_ROOT = WORKSPACE.getRoot();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.RelativePathService#roots()
	 */
	@Override
	public List<Path> roots() {
		final IProject project = adapt( IProject.class );
		List<Path> roots = new ArrayList<Path>();
		if ( project != null ) {
			IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
			for ( IClasspathEntry iClasspathEntry : cpEntries ) {
				if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
					IPath entryPath = WORKSPACE_ROOT.getFolder( iClasspathEntry.getPath() ).getLocation();
					roots.add( new Path( entryPath.toPortableString() ) );
				}
			}

		}
		return roots;

	}

}
