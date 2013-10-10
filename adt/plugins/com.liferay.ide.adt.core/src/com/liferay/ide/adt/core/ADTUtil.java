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
 *******************************************************************************/

package com.liferay.ide.adt.core;

import com.liferay.ide.core.util.ZipUtil;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ADTUtil
{

    private static final String[] liferayMobileSdkFiles =
    {
        "libs/liferay-android-sdk.jar",
        "libs/liferay-android-sdk.jar.properties",
        "libs/src/liferay-android-sdk-sources.jar"
    };

    // IDE-1179
    public static void addLiferayMobileSdkLibraries( final IProject project, IProgressMonitor monitor )
    {
        boolean monitorNull = monitor == null;

        try
        {
            final ZipFile projectTemplate = new ZipFile( ADTCore.getProjectTemplateFile() );
            final String zipTopLevelDir = ZipUtil.getFirstZipEntryName( ADTCore.getProjectTemplateFile() );

            IFile libFile = null;
            InputStream libInputStream = null;

            File libDir = null;

            for( String file : liferayMobileSdkFiles )
            {
                //Unzip lib file to project if it doesn't exist.
                libFile = project.getFile( file );

                if( ! libFile.exists() )
                {
                    // Create lib dir if it doesn't exist.
                    libDir = project.getLocation().append( new Path( file ).removeLastSegments( 1 ) ).toFile();

                    if( !libDir.exists() )
                    {
                        libDir.mkdirs();

                        try
                        {
                            project.refreshLocal( IResource.DEPTH_INFINITE, null );
                        }
                        catch( CoreException e)
                        {
                            ADTCore.logError( "Error refreshing local project.", e );
                        }
                    }

                    libInputStream = projectTemplate.getInputStream( new ZipEntry( zipTopLevelDir + file.toString() ) );
                    libFile.create( libInputStream, IResource.FORCE, null );
                }

                if( !monitorNull )
                {
                    monitor.worked( 10 / (liferayMobileSdkFiles.length ) );
                }
            }

            project.refreshLocal( IResource.DEPTH_INFINITE, null );
        }
        catch( CoreException e )
        {
            ADTCore.logError( "Error refreshing local project.", e );
        }
        catch( Exception e )
        {
            ADTCore.logError( "Error copying library files.", e );
        }

    }

    public static int extractSdkLevel( String content )
    {
        return Integer.parseInt( content.substring( content.indexOf( "API " ) + 4, content.indexOf( ":" ) ) );
    }

    public static boolean hasLiferayMobileSdkLibraries( final IProject project )
    {
        for( String lib : liferayMobileSdkFiles )
        {
            if( ! project.getFile( lib ).exists() )
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
