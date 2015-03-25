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

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Gregory Amerson
 */
public class GradleBndPluginProject extends BaseLiferayProject implements IBundleProject
{

    public GradleBndPluginProject( IProject project )
    {
        super( project );
    }

    @Override
    public boolean filterResource( IPath moduleRelativePath )
    {
        // TODO Auto-generated method stub
        return false;
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
    public IPath getOutputJar( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
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
    public String getSymbolicName() throws CoreException
    {
        // TODO Auto-generated method stub
        return null;
    }



}
