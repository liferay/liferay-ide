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

package com.liferay.ide.server.core.portal;

import aQute.remote.api.Agent;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class PortalServerDelegate extends ServerDelegate implements PortalServerWorkingCopy
{

    private int nextAgentPort = Agent.DEFAULT_PORT + 1;

    public PortalServerDelegate()
    {
        super();
    }

    @Override
    public IStatus canModifyModules( IModule[] add, IModule[] remove )
    {
        IStatus retval = Status.OK_STATUS;

        if( !CoreUtil.isNullOrEmpty( add ) )
        {
            for( IModule module : add )
            {
                if( !"liferay.bundle".equals( module.getModuleType().getId() ) )
                {
                    retval =
                        LiferayServerCore.error( "Unable to add module with type " + module.getModuleType().getName() );
                    break;
                }
            }
        }

        return retval;
    }

    public int getAutoPublishTime()
    {
        return getAttribute(Server.PROP_AUTO_PUBLISH_TIME, 0);
    }

    @Override
    public IModule[] getChildModules( IModule[] module )
    {
        IModule[] retval = null;

        if( module != null )
        {
            final IModuleType moduleType = module[0].getModuleType();

            if( module.length == 1 && moduleType != null && "liferay.bundle".equals( moduleType.getId() ) )
            {
                retval = new IModule[0];
            }
        }

        return retval;
    }

    @Override
    public boolean getDeveloperMode()
    {
        return getAttribute(
            PROPERTY_DEVELOPER_MODE, PortalServerConstants.DEFAULT_DEVELOPER_MODE );
    }

    public String getExternalProperties()
    {
        return getAttribute( PROPERTY_EXTERNAL_PROPERTIES, StringPool.EMPTY );
    }

    @Override
    public boolean getLaunchSettings()
    {
        return getAttribute( PROPERTY_LAUNCH_SETTINGS, PortalServerConstants.DEFAULT_LAUNCH_SETTING );
    }

    @Override
    public String getHttpPort()
    {
        // TODO IDE-1955
        return "8080";
    }

    public String getHost()
    {
        return getServer().getHost();
    }

    public String getId()
    {
        return getServer().getId();
    }

    @Override
    public void setLaunchSettings( boolean launchSettings )
    {
        setAttribute( PROPERTY_LAUNCH_SETTINGS, launchSettings );
    }

    public String[] getMemoryArgs()
    {
        String[] retval = null;

        final String args = getAttribute( PROPERTY_MEMORY_ARGS, PortalServerConstants.DEFAULT_MEMORY_ARGS );

        if( !CoreUtil.isNullOrEmpty( args ) )
        {
            retval = args.split( " " );
        }

        return retval;
    }

    public String getPassword()
    {
        return getAttribute( ATTR_PASSWORD, DEFAULT_PASSWORD );
    }

    @Override
    public URL getPluginContextURL( String context )
    {
        try
        {
            return new URL( getPortalHomeUrl(), StringPool.FORWARD_SLASH + context );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public URL getPortalHomeUrl()
    {
        try
        {
            return new URL("http://localhost:8080");
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public IModule[] getRootModules( IModule module ) throws CoreException
    {
        final IStatus status = canModifyModules( new IModule[] { module }, null );

        if( status == null || !status.isOK() )
        {
            throw new CoreException( status );
        }

        return new IModule[] { module };
    }

    public String getUsername()
    {
        return getAttribute( ATTR_USERNAME, DEFAULT_USERNAME );
    }

    @Override
    public URL getWebServicesListURL()
    {
        try
        {
            return new URL( getPortalHomeUrl(), "/tunnel-web/axis" ); //$NON-NLS-1$
        }
        catch( MalformedURLException e )
        {
            LiferayServerCore.logError( "Unable to get web services list URL", e ); //$NON-NLS-1$
        }

        return null;
    }

    @Override
    public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException
    {
    }

    @Override
    public void setDefaults( IProgressMonitor monitor )
    {
        setAttribute( Server.PROP_AUTO_PUBLISH_TIME, getAutoPublishTime() );
        setAttribute( "AGENT_PORT", nextAgentPort++ );
    }

    @Override
    public void setDeveloperMode( boolean developerMode )
    {
        setAttribute( PROPERTY_DEVELOPER_MODE, developerMode );
    }

    public void setExternalProperties( String externalProperties )
    {
        setAttribute( PROPERTY_EXTERNAL_PROPERTIES, externalProperties );
    }

    public void setMemoryArgs( String memoryArgs )
    {
        setAttribute( PROPERTY_MEMORY_ARGS, memoryArgs );
    }

    public void setPassword( String password )
    {
        setAttribute( ATTR_PASSWORD, password );
    }

    public void setUsername( String username )
    {
        setAttribute( ATTR_USERNAME, username );
    }

}