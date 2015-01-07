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
package com.liferay.ide.bndtools.core;

import aQute.bnd.build.Project;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.portal.BundleModulePublisher;
import com.liferay.ide.server.core.portal.ModulePublisher;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;


/**
 * @author Gregory Amerson
 */
public class BndtoolsProject extends BaseLiferayProject implements IBundleProject
{
    private final Project bndProject;

    public BndtoolsProject( IProject project, Project bndProject  )
    {
        super( project );
        this.bndProject = bndProject;
    }

    @Override
    public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        T retval = null;

        if( ModulePublisher.class.equals( adapterType ) )
        {
            return adapterType.cast( new BundleModulePublisher( this ) );
        }

        return retval;
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPath getLibraryPath( String filename )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProperty( String key, String defaultValue )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFile getOutputJar( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        IFile retval = null;

        try
        {
            final File[] buildFiles = this.bndProject.getBuildFiles( buildIfNeeded );

            if( !CoreUtil.isNullOrEmpty( buildFiles ) )
            {
                IPath projectLoc = this.getProject().getRawLocation();
                IPath buildFile = new Path( buildFiles[0].getCanonicalPath() );

                retval = this.getProject().getFile( buildFile.makeRelativeTo( projectLoc ) );
            }
        }
        catch( Exception e )
        {
            BndtoolsCore.logError( "Unable to get output jar for " + this.getProject().getName(), e );
        }

        return retval;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }


}
