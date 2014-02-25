/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LiferayRuntimeProject extends BaseLiferayProject
{

    private ILiferayRuntime liferayRuntime;

    public LiferayRuntimeProject( IProject project, ILiferayRuntime liferayRuntime )
    {
        super( project );

        this.liferayRuntime = liferayRuntime;
    }

    public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        if( IProjectBuilder.class.equals( adapterType ) )
        {
            final SDK sdk = getSDK();

            if( sdk != null )
            {
                final IProjectBuilder projectBuilder = new SDKProjectBuilder( getProject(), sdk );

                return adapterType.cast( projectBuilder );
            }
        }

        if( IRemoteServerPublisher.class.equals( adapterType ) )
        {
            final SDK sdk = getSDK();

            if( sdk != null )
            {
                final IRemoteServerPublisher remotePublisher = new SDKProjectRemoteServerPublisher( getProject(), sdk );

                return adapterType.cast( remotePublisher );
            }
        }
        return null;
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

    protected SDK getSDK()
    {
        SDK retval = null;

        // try to determine SDK based on project location
        IPath sdkLocation = getProject().getRawLocation().removeLastSegments( 2 );

        retval = SDKManager.getInstance().getSDK( sdkLocation );

        if( retval == null )
        {
            retval = SDKUtil.createSDKFromLocation( sdkLocation );
            SDKManager.getInstance().addSDK( retval );
        }

        return retval;
    }

    public IPath[] getUserLibs()
    {
        return this.liferayRuntime.getUserLibs();
    }

}
