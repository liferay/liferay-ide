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

import com.liferay.ide.project.core.BaseLiferayProject;
import com.liferay.ide.server.core.portal.ModulePublisher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 */
public class BundleProject extends BaseLiferayProject
{

    public BundleProject( IProject project )
    {
        super( project );
    }

    @Override
    public <T> T adapt( Class<T> adapterType )
    {
        final T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        if( ModulePublisher.class.equals( adapterType ) )
        {
            return adapterType.cast( new BundleModulePublisher( this ) );
        }

        return null;
    }

    public IResource findDocrootResource( IPath path )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerPortalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IFolder getDefaultDocrootFolder()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IFile getDescriptorFile( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getHookSupportedProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getLibraryPath( String filename )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPortalVersion()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getPortletCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getPortletEntryCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProperty( String key, String defaultValue )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IFolder[] getSourceFolders()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath[] getUserLibs()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean pathInDocroot( IPath fullPath )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Collection<IFile> getOutputs( boolean build, IProgressMonitor monitor ) throws CoreException
    {
        final Collection<IFile> outputs = new HashSet<IFile>();

        if( build )
        {
            this.getProject().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

            new MavenProjectBuilder( this.getProject() ).runMavenGoal( getProject(), "package", monitor );

            final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
            final MavenProject mavenProject = projectFacade.getMavenProject( monitor );
            final String targetFile = mavenProject.getBuild().getFinalName() + ".jar";

            // TODO don't hardcode target
            final IFile output = getProject().getFile( new Path( "target" ).append( targetFile ) );

            if( output.exists() )
            {
                outputs.add( output );
            }
        }

        return outputs;
    }

}
