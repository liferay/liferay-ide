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

package com.liferay.ide.adt.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ADTUtil
{
    public static void addLibsToAndroidProject( final IProject project, List<File[]> filesList, IProgressMonitor monitor )
        throws CoreException
    {
        final IFolder libsFolder = project.getFolder( "libs" );

        CoreUtil.makeFolders( libsFolder );

        final IFolder srcFolder = libsFolder.getFolder( "src" );

        CoreUtil.makeFolders( srcFolder );

        for( File[] files : filesList )
        {
            monitor.worked( 1 );
            monitor.subTask( "Added file " + files[0].getName() );

            FileUtil.copyFileToIFolder( files[0], libsFolder, monitor );
            FileUtil.copyFileToIFolder( files[1], srcFolder, monitor );

            final String propsFilename = files[0].getName() + ".properties";
            final String content = "src=src/" + files[1].getName();

            final IFile propsFile = libsFolder.getFile( propsFilename );

            if( propsFile.exists() )
            {
                propsFile.setContents( new ByteArrayInputStream( content.getBytes() ), true, true, monitor );
            }
            else
            {
                propsFile.create( new ByteArrayInputStream( content.getBytes() ), true, monitor );
            }
        }

        project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

        try
        {
            final IJavaProject javaProject = JavaCore.create( project );

            final IPath path = new Path( "com.android.ide.eclipse.adt.LIBRARIES" );

            final IClasspathContainer container = JavaCore.getClasspathContainer( path, javaProject );

            final ClasspathContainerInitializer initializer =
                JavaCore.getClasspathContainerInitializer( "org.eclipse.jst.j2ee.internal.web.container" ); //$NON-NLS-1$

            initializer.requestClasspathContainerUpdate( container.getPath(), javaProject, container );
        }
        catch( Exception e )
        {
            // ignore errors since this is best effort
        }
    }

    public static int extractSdkLevel( String content )
    {
        return Integer.parseInt( content.substring( content.indexOf( "API " ) + 4, content.indexOf( ":" ) ) );
    }

    public static boolean hasLiferayMobileSdkLibraries( final IProject project )
    {
        for( String lib : MobileSDKCore.getLibraryMap().keySet() )
        {
            if( ! project.getFolder( "libs" ).getFile( lib ).exists() )
            {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings( "restriction" )
    public static boolean isAndroidProject( final IProject project )
    {
        boolean retval = false;

        try
        {
            retval = project.hasNature( com.android.ide.eclipse.adt.AdtConstants.NATURE_DEFAULT );
        }
        catch( CoreException e )
        {
        }

        return retval;
    }

}
