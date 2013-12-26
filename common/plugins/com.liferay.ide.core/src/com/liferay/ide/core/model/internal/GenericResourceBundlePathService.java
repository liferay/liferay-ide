/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintanence
 *******************************************************************************/

package com.liferay.ide.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class GenericResourceBundlePathService extends RelativePathService
{

    public static final String RB_FILE_EXTENSION = "properties"; //$NON-NLS-1$

    private final IWorkspaceRoot WORKSPACE_ROOT = CoreUtil.getWorkspaceRoot();

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
    public Path convertToRelative( Path path )
    {
        final Path localPath = super.convertToRelative( path );
        final String bundle = localPath.toPortableString();
        if ( bundle != null && bundle.indexOf( "/" ) != -1 )
        {
            final String correctBundle = bundle.replace( "/", "." );
            Path newPath = Path.fromPortableString( correctBundle );
            return newPath.removeFileExtension();
        }
        
        return localPath;
    }


    /**
     * This method is used to get the IProject handle of the project relative to which the source paths needs to be
     * computed
     *
     * @return handle to IProject
     */
    protected IProject project()
    {
        return context( Element.class ).adapt( IProject.class );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.services.RelativePathService#roots()
     */
    @Override
    public final List<Path> roots()
    {
        return computeRoots( project() );
    }

}
