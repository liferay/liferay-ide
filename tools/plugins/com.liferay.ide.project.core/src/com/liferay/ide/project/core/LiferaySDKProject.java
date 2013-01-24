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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


/**
 * @author Gregory Amerson
 */
public class LiferaySDKProject implements ILiferayProject
{

    private IProject project;

    public LiferaySDKProject( IProject project )
    {
        this.project = project;
    }

    public IPath getAppServerPortalDir()
    {
        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( this.project );
            return liferayRuntime.getAppServerPortalDir();
        }
        catch( Exception e )
        {
            LiferayProjectCore.logError( "Unable to getAppServerPortalDir", e ); //$NON-NLS-1$
        }

        return null;
    }

    public String[] getHookSupportedProperties()
    {
        ILiferayRuntime liferayRuntime = getLiferayRuntime();

        if( liferayRuntime != null )
        {
            return liferayRuntime.getHookSupportedProperties();
        }

        return null;
    }

    public IPath getLibraryPath( String filename )
    {
        final IPath[] libs = getUserLibs();

        if( ! CoreUtil.isNullOrEmpty( libs ) )
        {
            for( IPath lib : libs )
            {
                if( lib.lastSegment().equals( filename ) )
                {
                    return lib;
                }
            }
        }

        return null;
    }

    private ILiferayRuntime getLiferayRuntime()
    {
        try
        {
            return ServerUtil.getLiferayRuntime( this.project );
        }
        catch( CoreException e )
        {
            LiferayProjectCore.logError( "Unable to get liferay runtime", e ); //$NON-NLS-1$
        }

        return null;
    }

    public String getPortalVersion()
    {
        ILiferayRuntime liferayRuntime = getLiferayRuntime();

        if( liferayRuntime != null )
        {
            return liferayRuntime.getPortalVersion();
        }

        return null;
    }

    public Properties getPortletCategories()
    {
        ILiferayRuntime liferayRuntime = getLiferayRuntime();

        if( liferayRuntime != null )
        {
            return getLiferayRuntime().getPortletCategories();
        }

        return null;
    }

    public Properties getPortletEntryCategories()
    {
        ILiferayRuntime liferayRuntime = getLiferayRuntime();

        if( liferayRuntime != null )
        {
            return getLiferayRuntime().getPortletEntryCategories();
        }

        return null;
    }

    public IPath[] getUserLibs()
    {
        try
        {
            ILiferayRuntime runtime = ServerUtil.getLiferayRuntime( project );

            return runtime.getAllUserClasspathLibraries();
        }
        catch( CoreException e )
        {
            LiferayProjectCore.logError( "Unable to get user libs", e ); //$NON-NLS-1$
        }

        return null;
    }

}
