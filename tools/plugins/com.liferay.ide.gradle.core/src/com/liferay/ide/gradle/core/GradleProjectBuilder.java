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

package com.liferay.ide.gradle.core;

import com.liferay.ide.project.core.AbstractProjectBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Terry Jia
 */
public class GradleProjectBuilder extends AbstractProjectBuilder
{

    private IFile gradleBuildFile;

    public GradleProjectBuilder( IProject project )
    {
        super( project );

        gradleBuildFile = project.getFile( "build.gradle" );
    }

    @Override
    public IStatus buildLang( IFile langFile, IProgressMonitor monitor ) throws CoreException
    {
        return runGradleTask( "buildLang", monitor );
    }

    @Override
    public IStatus buildService( IProgressMonitor monitor ) throws CoreException
    {
        return runGradleTask( "buildService", monitor );
    }

    @Override
    public IStatus buildWSDD( IProgressMonitor monitor ) throws CoreException
    {
        // TODO Waiting for IDE-2850
        return null;
    }

    private IStatus runGradleTask( String task, IProgressMonitor monitor )
    {
        IStatus status = Status.OK_STATUS;

        if( gradleBuildFile.exists() )
        {
            try
            {
                monitor.beginTask( task, 100 );

                GradleUtil.runGradleTask( getProject(), task, monitor );

                monitor.worked( 80 );

                getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );

                monitor.worked( 10 );
            }
            catch( Exception e )
            {
                status = GradleCore.createErrorStatus( "Error running Gradle goal " + task, e );
            }
        }
        else
        {
            status = GradleCore.createErrorStatus( "No build.gradle file" );
        }

        return status;
    }

    @Override
    public IStatus creatInitBundle( IProject project, String taskName,  String bundleUrl, IProgressMonitor monitor ) throws CoreException
    {
        final File gradlePropertiesFile = project.getFile( "gradle.properties" ).getLocation().toFile();

        try(InputStream in = new FileInputStream( gradlePropertiesFile );
                        OutputStream out = new FileOutputStream( gradlePropertiesFile ))
        {
            final Properties properties = new Properties();
            properties.load( in );

            properties.put( "liferay.workspace.bundle.url", bundleUrl );

            properties.store( out, "" );

            runGradleTask( taskName, monitor );

            project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( IOException e )
        {
        }
        return Status.OK_STATUS;
    }
}
