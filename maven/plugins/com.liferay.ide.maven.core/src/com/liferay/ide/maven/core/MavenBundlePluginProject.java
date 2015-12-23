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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.maven.core.util.DefaultMaven2OsgiConverter;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 */
public class MavenBundlePluginProject extends LiferayMavenProject implements IBundleProject
{
    private final String[] ignorePaths = new String[] { "target" };

    public MavenBundlePluginProject( IProject project )
    {
        super( project );
    }

    public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( getProject(), new NullProgressMonitor() );

        if( facade != null )
        {
            if( IProjectBuilder.class.equals( adapterType ) )
            {
                final IProjectBuilder projectBuilder = new MavenProjectBuilder( getProject() );

                return adapterType.cast( projectBuilder );
            }
            else if( IRemoteServerPublisher.class.equals( adapterType ) )
            {
                final IRemoteServerPublisher remoteServerPublisher =
                    new MavenProjectRemoteServerPublisher( getProject() );

                return adapterType.cast( remoteServerPublisher );
            }
            else if( IBundleProject.class.equals( adapterType ) )
            {
                return adapterType.cast( this );
            }
        }

        return null;
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPath getOutputBundle( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        IPath outputJar = null;

        if( buildIfNeeded )
        {
            final MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder( this.getProject() );

            // TODO update status
            final List<String> goals = Arrays.asList( "package" );

            try
            {
                mavenProjectBuilder.execGoals( goals, monitor );
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError( "Error building package", e );
            }
        }

        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
        final MavenProject mavenProject = projectFacade.getMavenProject( monitor );

        final String targetName = mavenProject.getBuild().getFinalName() + ".jar";

        // TODO find a better way to get the target folder
        final IFolder targetFolder = getProject().getFolder( "target" );

        if( targetFolder.exists() )
        {
            // targetFolder.refreshLocal( IResource.DEPTH_ONE, monitor );
            final IPath targetFile = targetFolder.getRawLocation().append( targetName );

            if( targetFile.toFile().exists() )
            {
                outputJar = targetFile;
            }
        }

        return outputJar;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        String retval = null;

        final IProgressMonitor monitor = new NullProgressMonitor();
        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
        final MavenProject mavenProject = projectFacade.getMavenProject( monitor );

        final Artifact artifact = mavenProject.getArtifact();
        final File file = artifact.getFile();

        if( file != null && file.exists() && !artifact.getFile().getName().equals( "classes" ) )
        {
            retval = new DefaultMaven2OsgiConverter().getBundleSymbolicName( artifact );
        }
        else
        {
            // fallback to project name
            retval = getProject().getLocation().lastSegment();
        }

        return retval;
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

}
