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

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import aQute.bnd.osgi.Jar;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayGradleProject extends BaseLiferayProject implements IBundleProject
{

    private static final String[] ignorePaths = new String[] { ".gradle", "build" };

    public LiferayGradleProject( IProject project )
    {
        super( project );
    }

    @Override
    public boolean filterResource( IPath resourcePath )
    {
        if( filterResource( resourcePath, ignorePaths ) )
        {
            return true;
        }

        return false;
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        return null;
    }

    @Override
    public IPath getLibraryPath( String filename )
    {
        return null;
    }

    @Override
    public IPath getOutputBundle( boolean build, IProgressMonitor monitor ) throws CoreException
    {
        IPath outputBundle = getOutputBundle( getProject() );

        if( !build && outputBundle != null && outputBundle.toFile().exists() )
        {
            return outputBundle;
        }
        else
        {
            final String task = isThemeProject( getProject() ) ? "build" : "jar";
            ProjectConnection connection = null;

            try
            {
                GradleConnector connector =
                    GradleConnector.newConnector().forProjectDirectory( getProject().getLocation().toFile() );

                connection = connector.connect();

                BuildLauncher launcher = connection.newBuild();
                launcher.forTasks( task ).run();
            }
            finally
            {
                connection.close();
            }

            outputBundle = getOutputBundle( getProject() );
        }

        if( outputBundle.toFile().exists() )
        {
            return outputBundle;
        }

        return null;
    }

    private boolean isThemeProject( IProject project )
    {
        return project.getFile( "gulpfile.js" ).exists() &&
            project.getFile( "package.json" ).exists() &&
            project.getFile( "src/WEB-INF/liferay-plugin-package.properties" ).exists();
    }

    @Override
    public String getProperty( String key, String defaultValue )
    {
        return null;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        String retval = null;

        final IPath outputBundle = getOutputBundle( getProject() );

        if( outputBundle == null || outputBundle.lastSegment().endsWith( ".war" ) )
        {
            return getProject().getName();
        }
        else if( outputBundle != null && outputBundle.toFile().exists() )
        {
            try( final Jar jar = new Jar( outputBundle.toFile() ) )
            {
                retval = jar.getBsn();
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

    public static IPath getOutputBundle( IProject gradleProject )
    {
        final CustomModel model = GradleCore.getToolingModel(
            GradleCore.getDefault(), CustomModel.class, gradleProject );

        Set<File> outputFiles = model.getOutputFiles();

        if( outputFiles.size() > 0 )
        {
            return new Path( outputFiles.iterator().next().getAbsolutePath() );
        }
        else if( model.hasPlugin( "com.liferay.gradle.plugins.gulp.GulpPlugin" ) )
        {
            return gradleProject.getLocation().append( "dist/" + gradleProject.getName() + ".war" );
        }

        return null;
    }

}