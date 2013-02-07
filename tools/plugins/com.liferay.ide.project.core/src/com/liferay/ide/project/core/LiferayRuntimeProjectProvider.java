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


/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeProjectProvider extends AbstractLiferayProjectProvider
{

    public LiferayRuntimeProjectProvider()
    {
        super(IProject.class);
    }

    public ILiferayProject provide( Object type )
    {
        final IProject project = (IProject) type;

        ILiferayRuntime liferayRuntime = null;

        try
        {
            liferayRuntime = ServerUtil.getLiferayRuntime( project );
        }
        catch( CoreException e )
        {
        }

        if( liferayRuntime != null )
        {
            return new LiferayRuntimeProject( liferayRuntime );
        }

        return null;
    }

}
