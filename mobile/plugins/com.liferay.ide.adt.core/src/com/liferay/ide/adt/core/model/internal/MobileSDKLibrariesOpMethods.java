/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.core.model.Library;
import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.mobile.sdk.core.PortalAPI;
import com.liferay.mobile.sdk.core.MobileSDKBuilder;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class MobileSDKLibrariesOpMethods
{

    private static void addLibsToAndroidProject( final IProject project, List<File[]> filesList, IProgressMonitor monitor )
        throws CoreException
    {
        final IFolder libsFolder = project.getFolder( "libs" );

        CoreUtil.makeFolders( libsFolder );

        final IFolder srcFolder = libsFolder.getFolder( "src" );

        CoreUtil.makeFolders( srcFolder );

        for( File[] files : filesList )
        {
            FileUtil.copyFileToIFolder( files[0], libsFolder, monitor );
            FileUtil.copyFileToIFolder( files[1], srcFolder, monitor );

            final String propsFilename = files[0].getName() + ".properties";
            final String content = "src=src/" + files[1].getName();

            libsFolder.getFile( propsFilename ).create( new ByteArrayInputStream( content.getBytes() ), true, monitor );
        }
    }

    public static final Status execute( final MobileSDKLibrariesOp op, final ProgressMonitor monitor )
    {
        Status retval = null;

        final IProject project = CoreUtil.getProject( op.getProjectName().content() );

        final Map<String, File[]> libs = MobileSDKCore.getLibraryMap();

        final ElementList<Library> libraries = op.getLibraries();

        if( libraries.size() == 0 )
        {
            libraries.insert().setContext( PortalAPI.NAME );
        }

        final Map<String, String[]> buildSpec = new HashMap<String, String[]>();

        boolean hasPortal = false;

        for( final Library library : libraries )
        {
            final String context = library.getContext().content();

            if( PortalAPI.NAME.equals( library.getContext().content() ) )
            {
                hasPortal = true;
                buildSpec.put( context, null );
            }
            else if( ! library.getEntity().empty() )
            {
                final String entity = library.getEntity().content();
                final String[] entities = buildSpec.get( context );

                if( CoreUtil.isNullOrEmpty( entities ) )
                {
                    buildSpec.put( context, new String[] { entity } );
                }
                else
                {
                    final String[] newEntities = new String[ entities.length + 1 ];
                    System.arraycopy( entities, 0, newEntities, 0, entities.length );
                    newEntities[ entities.length ] = entity;

                    buildSpec.put( context, newEntities );
                }
            }
        }

        File[] customJars = null;

        if( ( hasPortal && buildSpec.keySet().size() > 1 ) || ( !hasPortal && buildSpec.keySet().size() > 0 ) )
        {
            customJars = MobileSDKBuilder.buildJars( op.getUrl().content(), op.getPackage().getDefaultText(), buildSpec );
        }

        final List<File[]> files = new ArrayList<File[]>();

        if( customJars != null )
        {
            files.add( customJars );
        }

        if( hasPortal )
        {
            // add standard sdk and sources
            files.add( libs.get( "liferay-android-sdk-1.1" ) );
        }
        else
        {
            files.add( libs.get( "liferay-android-sdk-1.1-core" ) );
        }

        try
        {
            addLibsToAndroidProject( project, files, ProgressMonitorBridge.create( monitor ) );

            retval = Status.createOkStatus();
        }
        catch( CoreException e )
        {
            retval = StatusBridge.create( ADTCore.createErrorStatus( "Could not add mobile sdk libraries.", e ) );
        }

        return retval;
    }

    public static void updateServerStatus( MobileSDKLibrariesOp op )
    {
        op.getStatus().service( StatusDerivedValueService.class ).updateStatus();
        op.getSummary().service( SummaryDerivedValueService.class ).updateStatus();
    }
}
