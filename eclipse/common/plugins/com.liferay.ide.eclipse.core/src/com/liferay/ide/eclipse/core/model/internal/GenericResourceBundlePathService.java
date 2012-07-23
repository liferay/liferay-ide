/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.core.model.internal;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;

/**
 * @author Kamesh Sampath
 */
public abstract class GenericResourceBundlePathService extends RelativePathService
{

    public static final String RB_FILE_EXTENSION = "properties";
    final IWorkspaceRoot WORKSPACE_ROOT = CoreUtil.getWorkspaceRoot();

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.services.RelativePathService#roots()
     */
    @Override
    public final List<Path> roots()
    {
        IProject project = project();
        List<Path> roots = computeRoots( project );
        return roots;
    }

    /**
     * This method is used to get the IProject handle of the project relative to which the source paths needs to be
     * computed
     * 
     * @return handle to IProject
     */
    protected abstract IProject project();

    /**
     * @param project
     * @return
     */
    final List<Path> computeRoots( IProject project )
    {
        List<Path> roots = new ArrayList<Path>();
        if( project != null )
        {
            IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries( project );
            for( IClasspathEntry iClasspathEntry : cpEntries )
            {
                if( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() )
                {
                    IPath entryPath = WORKSPACE_ROOT.getFolder( iClasspathEntry.getPath() ).getLocation();
                    String fullPath = entryPath.toOSString();
                    Path sapphirePath = new Path( fullPath );
                    roots.add( sapphirePath );
                }
            }

        }
        return roots;
    }

    @Override
    public final Path convertToAbsolute( Path path )
    {
        Path absPath = path.addFileExtension( RB_FILE_EXTENSION );
        return absPath;
    }

}
