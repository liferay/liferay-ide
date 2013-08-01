/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IRuntime;


/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeProjectProvider extends AbstractLiferayProjectProvider
{

    public LiferayRuntimeProjectProvider()
    {
        super( new Class<?>[] { IProject.class, IRuntime.class } );
    }

    public ILiferayProject provide( Object type )
    {
        LiferayRuntimeProject retval = null;
        IProject project = null;
        ILiferayRuntime liferayRuntime = null;

        if( type instanceof IProject )
        {
            project = (IProject) type;

            try
            {
                liferayRuntime = ServerUtil.getLiferayRuntime( project );
            }
            catch( CoreException e )
            {
            }
        }
        else if( type instanceof IRuntime )
        {
            try
            {
                final IRuntime runtime = (IRuntime) type;

                liferayRuntime = ServerUtil.getLiferayRuntime( runtime );
            }
            catch( Exception e )
            {
            }
        }

        if( liferayRuntime != null )
        {
            retval = new LiferayRuntimeProject( project, liferayRuntime );
        }

        return retval;
    }

}
