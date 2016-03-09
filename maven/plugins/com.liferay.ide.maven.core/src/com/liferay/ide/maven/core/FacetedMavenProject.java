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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.IResourceBundleProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.project.core.FlexibleProject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
public class FacetedMavenProject extends LiferayMavenProject implements IWebProject, IResourceBundleProject
{

    private final FlexibleProject flexibleProject;

    public FacetedMavenProject( IProject project )
    {
        super( project );

        this.flexibleProject = new FlexibleProject( project )
        {
            @Override
            public IPath getLibraryPath( String filename )
            {
                return null;
            }

            @Override
            public String getProperty( String key, String defaultValue )
            {
                return null;
            }
        };
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
            if( ILiferayPortal.class.equals( adapterType ) )
            {
                final ILiferayPortal portal = new LiferayPortalMaven( this );

                return adapterType.cast( portal );
            }
        }

        return null;
    }

    @Override
    public IResource findDocrootResource( IPath path )
    {
        return this.flexibleProject.findDocrootResource( path );
    }

    @Override
    public IFolder getDefaultDocrootFolder()
    {
        return this.flexibleProject.getDefaultDocrootFolder();
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        return this.flexibleProject.getDescriptorFile( name );
    }

    public Collection<IFile> getOutputs( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        final Collection<IFile> outputs = new HashSet<IFile>();

        if( buildIfNeeded )
        {
            this.getProject().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

            new MavenProjectBuilder( this.getProject() ).runMavenGoal( getProject(), "package", monitor );

            final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
            final MavenProject mavenProject = projectFacade.getMavenProject( monitor );
            final String targetFolder = mavenProject.getBuild().getDirectory();
            final String targetWar = mavenProject.getBuild().getFinalName() + "." + mavenProject.getPackaging();

            final IFile output = getProject().getFile( new Path( targetFolder ).append( targetWar ) );

            if( output.exists() )
            {
                outputs.add( output );
            }
        }

        return outputs;
    }

    @Override
    public List<IFile> getDefaultLanguageProperties()
    {
        return flexibleProject.getDefaultLanguageProperties();
    }

}
