/*******************************************************************************
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
 *
 *******************************************************************************/
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

public class ClasspathUtil
{

    public static boolean isPluginContainerEntry( final IClasspathEntry e )
    {
        return e != null && e.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
            e.getPath().segment( 0 ).equals( SDKClasspathContainer.ID );
    }

    public static boolean hasNewLiferaySDKContainer( final IClasspathEntry[] entries )
    {
        boolean retVal = false;
        for( IClasspathEntry entry : entries )
        {
            if( entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
                entry.getPath().segment( 0 ).equals( SDKClasspathContainer.ID ) )
            {
                retVal = true;
                break;
            }
        }

        return retVal;
    }

    public static void updateRequestContainer(IProject project) throws CoreException
    {
        final IJavaProject javaProject = JavaCore.create( project );
        IPath containerPath = null;

        final IClasspathEntry[] entries = javaProject.getRawClasspath();

        for( final IClasspathEntry entry : entries )
        {
            if( entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER )
            {
                if( entry.getPath().segment( 0 ).equals( SDKClasspathContainer.ID ) )
                {
                    containerPath = entry.getPath();
                    break;
                }
            }
        }

        if( containerPath != null )
        {
            final IClasspathContainer classpathContainer = JavaCore.getClasspathContainer( containerPath, javaProject );

            final String id = containerPath.segment( 0 );

            if ( id.equals( SDKClasspathContainer.ID ) )
            {
                ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer( id );
                initializer.requestClasspathContainerUpdate( containerPath, javaProject, classpathContainer );
            }
        }
    }
}
