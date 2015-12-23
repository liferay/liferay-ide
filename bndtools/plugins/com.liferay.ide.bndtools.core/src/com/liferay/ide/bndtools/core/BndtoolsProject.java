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

import java.io.File;
import java.util.Collection;

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
    public IPath getOutputBundle( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
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

        return retval.getRawLocation();
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        String retval = this.bndProject.getName();
        try
        {
            final Collection<String> names = this.bndProject.getBsns();

            if( names != null && names.size() > 0 )
            {
                retval = names.iterator().next();
            }
        }
        catch( Exception e )
        {
        }

        return retval;
    }

    @Override
    public boolean filterResource( IPath moduleRelativePath )
    {
        // TODO Auto-generated method stub
        return false;
    }


}
