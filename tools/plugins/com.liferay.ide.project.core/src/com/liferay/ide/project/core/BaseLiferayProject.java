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

package com.liferay.ide.project.core;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectAdapter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

/**
 * @author Gregory Amerson
 */
public abstract class BaseLiferayProject implements ILiferayProject
{

    private IProject project;

    public BaseLiferayProject( IProject project )
    {
        this.project = project;
    }

    public <T> T adapt( Class<T> adapterType )
    {
        final ILiferayProjectAdapter[] adapters = LiferayCore.getProjectAdapters();

        if( ! CoreUtil.isNullOrEmpty( adapters ) )
        {
            for( ILiferayProjectAdapter adapter : adapters )
            {
                T adapted = adapter.adapt( this, adapterType );

                if( adapted != null )
                {
                    return adapted;
                }
            }
        }

        return null;
    }

    public IProject getProject()
    {
        return this.project;
    }

    public IFolder getSourceFolder( String classification )
    {
        return CoreUtil.getFirstSrcFolder( project );
    }

}
