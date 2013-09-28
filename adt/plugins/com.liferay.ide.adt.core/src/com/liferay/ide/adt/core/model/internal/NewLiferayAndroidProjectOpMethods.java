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
package com.liferay.ide.adt.core.model.internal;


import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.core.ADTUtil;
import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewLiferayAndroidProjectOpMethods
{
    private static File createProjectDir(final NewLiferayAndroidProjectOp op)
    {
        File projectDir = null;

        if( op.getUseDefaultLocation().content( true ) )
        {
            projectDir = CoreUtil.getWorkspaceRoot().getLocation().append( op.getProjectName().content() ).toFile();
        }
        else
        {
            projectDir = PathBridge.create( op.getLocation().content() ).toFile();
        }

        projectDir.mkdirs();

        return projectDir;
    }

    public static final Status execute( final NewLiferayAndroidProjectOp op, final ProgressMonitor monitor )
    {
        final IProgressMonitor platformMonitor = ProgressMonitorBridge.create( monitor );
        platformMonitor.beginTask( "Creating Liferay Android project", 100 );

        File projectDir = createProjectDir( op );

        File projectTemplate = ADTCore.getProjectTemplateFile();

        try
        {
            final String topLevelDir = ZipUtil.getFirstZipEntryName( projectTemplate );

            ZipUtil.unzip( projectTemplate, topLevelDir, projectDir, platformMonitor );

            updateProjectContents( projectDir, op );

            openProject( projectDir, op, platformMonitor );
        }
        catch( Exception e )
        {
            return Status.createErrorStatus( "Error creating Liferay Android project", e );
        }

        return Status.createOkStatus();
    }

    private static void openProject( File projectDir, NewLiferayAndroidProjectOp op, final IProgressMonitor monitor )
        throws CoreException
    {
        final IProject newProject = workspaceRoot().getProject( op.getProjectName().content() );

        final IProjectDescription description = workspace().newProjectDescription( op.getProjectName().content() );

        if( op.getUseDefaultLocation().content( true ) )
        {
            description.setLocation( null );
        }
        else
        {
            description.setLocationURI( projectDir.toURI() );
        }

        newProject.create( description, monitor );
        newProject.open( monitor );
    }

    private static void updateProjectContents( File projectDir, NewLiferayAndroidProjectOp op )
        throws FileNotFoundException, IOException
    {
        File dotProject = new File( projectDir, ".project" );

        FileUtil.searchAndReplace( dotProject, "<name>sample-android-app</name>", "<name>" +
            op.getProjectName().content() + "</name>" );

        File androidManfest = new File( projectDir, "AndroidManifest.xml" );
        File projectProperties = new File( projectDir, "project.properties" );

        int sdkLevel = ADTUtil.extractSdkLevel( op.getTargetSDK().content( true ) );

        FileUtil.searchAndReplace( androidManfest, "android:targetSdkVersion=\"17\"", "android:targetSdkVersion=\"" +
            sdkLevel + "\"" );
        FileUtil.searchAndReplace( projectProperties, "target=android-17", "target=android-" + sdkLevel );
    }

    private static IWorkspace workspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    private static IWorkspaceRoot workspaceRoot()
    {
        return workspace().getRoot();
    }
}
