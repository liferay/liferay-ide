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

import java.util.Properties;

import org.eclipse.core.runtime.IPath;


/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeProject implements ILiferayProject
{

    private ILiferayRuntime liferayRuntime;

    public LiferayRuntimeProject( ILiferayRuntime liferayRuntime )
    {
        this.liferayRuntime = liferayRuntime;
    }
    public IPath getAppServerPortalDir()
    {
        return this.liferayRuntime.getAppServerPortalDir();
    }

    public String[] getHookSupportedProperties()
    {
        return liferayRuntime.getHookSupportedProperties();
    }

    public IPath getLibraryPath( String filename )
    {
        final IPath[] libs = getUserLibs();

        if( ! CoreUtil.isNullOrEmpty( libs ) )
        {
            for( IPath lib : libs )
            {
                if( lib.lastSegment().startsWith( filename ) )
                {
                    return lib;
                }
            }
        }

        return null;
    }

    public String getPortalVersion()
    {
        return liferayRuntime.getPortalVersion();
    }

    public Properties getPortletCategories()
    {
        return this.liferayRuntime.getPortletCategories();
    }

    public Properties getPortletEntryCategories()
    {
        return this.liferayRuntime.getPortletEntryCategories();
    }

    public IPath[] getUserLibs()
    {
        return this.liferayRuntime.getUserLibs();
    }

}
