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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceProject extends BaseLiferayProject
{

    public LiferayWorkspaceProject( IProject project )
    {
        super( project );
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
    public String getProperty( String key, String defaultValue )
    {
        return null;
    }

}
