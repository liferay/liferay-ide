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

import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.AbstractProjectBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
    public IStatus execInitBundle( IProject project, String taskName,  String bundleUrl, IProgressMonitor monitor ) throws CoreException
    {
        String bundleUrlProperty = "\n\nliferay.workspace.bundle.url=" + bundleUrl;

        final File gradlePropertiesFile = project.getFile( "gradle.properties" ).getLocation().toFile();

        try
        {
            Files.write( gradlePropertiesFile.toPath(), bundleUrlProperty.getBytes(), StandardOpenOption.APPEND );
        }
        catch( IOException e )
        {
            GradleCore.logError( "Error append bundle url property", e );
        }

        runGradleTask( taskName, monitor );

        project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

        return Status.OK_STATUS;
    }

    @Override
    public IStatus updateProjectDependency( IProject project, List<String[]> dependencies ) throws CoreException
    {
        try
        {
            if( gradleBuildFile.exists() )
            {
                GradleDependencyUpdater updater = new GradleDependencyUpdater( gradleBuildFile.getLocation().toFile() );
                List<GradleDependency> existDependencies = updater.getAllDependencies();

                for( String[] dependency : dependencies )
                {
                    GradleDependency gd = new GradleDependency( dependency[0], dependency[1], dependency[2] );

                    if( !existDependencies.contains( gd ) )
                    {
                        updater.insertDependency( gd );

                        FileUtils.writeLines( gradleBuildFile.getLocation().toFile(), updater.getGradleFileContents() );

                        GradleUtil.refreshGradleProject( project );
                    }
                }
            }
        }
        catch( IOException e )
        {
            return GradleCore.createErrorStatus( "Error updating gradle project dependency", e );
        }

        return Status.OK_STATUS;
    }
}
