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
import com.liferay.ide.adt.core.model.ServerInstance;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.ResourceStoreException;
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

    private static boolean containsInstance( MobileSDKLibrariesOp op, ElementList<ServerInstance> instances )
    {
        for( ServerInstance instance : instances )
        {
            if( instance.getUrl().content().equals( op.getUrl().content() ) )
            {
                return true;
            }
        }

        return false;
    }

    public static final Status execute( final MobileSDKLibrariesOp op, final ProgressMonitor monitor )
    {
        Status retval = null;

        saveWizardSettings( op );

        final IProject project = CoreUtil.getProject( op.getProjectName().content() );

        final Map<String, File[]> libs = MobileSDKCore.getLibraryMap();

        final ElementList<Library> libraries = op.getLibraryNames();

        final List<File[]> files = new ArrayList<File[]>();

        for( Library library : libraries )
        {
            final String libName = library.getName().content();

            if( libs.containsKey( libName ) )
            {
                files.add( libs.get( libName ) );
            }
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

    private static void saveWizardSettings( final MobileSDKLibrariesOp op )
    {
        if( ! CoreUtil.isNullOrEmpty( op.getUrl().content() ) )
        {
            try
            {
                final ElementList<ServerInstance> previousServerInstances = op.getPreviousServerInstances();

                if( ! containsInstance( op, previousServerInstances ) )
                {
                    op.getPreviousServerInstances().insert().copy( op );
                }

                op.resource().save();
            }
            catch( ResourceStoreException e )
            {
                ADTCore.logError( "Unable to persist wizard settings", e );
            }
        }
    }

    public static void updateServerStatus( MobileSDKLibrariesOp op )
    {
        op.getStatus().service( StatusDerivedValueService.class ).updateStatus();
        op.getSummary().service( SummaryDerivedValueService.class ).updateStatus();
    }
}
