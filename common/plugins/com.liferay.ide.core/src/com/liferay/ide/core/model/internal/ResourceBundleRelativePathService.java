/*******************************************************************************
 * Copyright (c) 20002011 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath  initial implementation
 *******************************************************************************/

package com.liferay.ide.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class ResourceBundleRelativePathService extends RelativePathService
{

	public static final String RB_FILE_EXTENSION = "properties";
	final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();
	final IWorkspaceRoot WORKSPACE_ROOT = WORKSPACE.getRoot();

	public List<Path> roots()
	{
		List<Path> roots = computeRoots();
		return roots;

	}

	private List<Path> computeRoots()
	{
		final IProject project = context( IProject.class );
		List<Path> roots = new ArrayList<Path>();
		if ( project != null )
		{
			IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
			for ( IClasspathEntry iClasspathEntry : cpEntries )
			{
				if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
				{
					IPath entryPath = WORKSPACE_ROOT.getFolder( iClasspathEntry.getPath() ).getLocation();
					roots.add( new Path( entryPath.toPortableString() ) );
				}
			}

		}
		return roots;

	}

	@Override
	public Path convertToAbsolute( Path path )
	{
		Path absPath = path.addFileExtension( "properties" );
		return absPath;
	}

}
