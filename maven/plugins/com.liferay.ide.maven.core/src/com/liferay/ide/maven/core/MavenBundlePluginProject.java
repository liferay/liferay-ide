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
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.server.core.portal.ModulePublisher;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 */
public class MavenBundlePluginProject extends LiferayMavenProject implements IBundleProject
{

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
            else if( ModulePublisher.class.equals( adapterType ) )
            {
                return adapterType.cast( new BundleModulePublisher( this ) );
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
    public IFile getOutputJar( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        IFile outputJar = null;

        if( buildIfNeeded )
        {
            try
            {
                this.getProject().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );
            }
            catch( CoreException e )
            {
            }

            final MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder( this.getProject() );

            final IStatus status = mavenProjectBuilder.execGoal( "package", monitor );

            if( status != null && status.isOK() )
            {
                final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
                final MavenProject mavenProject = projectFacade.getMavenProject( monitor );

                final String targetName = mavenProject.getBuild().getFinalName() + ".jar";

                // TODO find a better way to get the target folder
                final IFolder targetFolder = getProject().getFolder( "target" );

                if( targetFolder.exists() )
                {
                    targetFolder.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                    final IFile targetFile = targetFolder.getFile( targetName );

                    if( targetFile.exists() )
                    {
                        outputJar = targetFile;
                    }
                }
            }
        }

        return outputJar;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        final IProgressMonitor monitor = new NullProgressMonitor();
        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
        final MavenProject mavenProject = projectFacade.getMavenProject( monitor );

        // TODO this may not necessarily be the project name

        return mavenProject.getArtifactId();
    }

}
