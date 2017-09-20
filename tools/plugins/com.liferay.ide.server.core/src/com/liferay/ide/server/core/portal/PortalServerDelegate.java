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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.list.SetUniqueList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerDelegate;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class PortalServerDelegate extends ServerDelegate implements PortalServerWorkingCopy
{

    private final static List<String> SUPPORT_TYPES_LIST = Arrays.asList( "liferay.bundle", "jst.web", "jst.utility" );

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
                if( !SUPPORT_TYPES_LIST.contains( module.getModuleType().getId() ) )
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
        return getAttribute( Server.PROP_AUTO_PUBLISH_TIME, 0 );
    }

    @Override
    public int getHttpPort()
    {
        int httpPort = ILiferayServer.DEFAULT_HTTP_PORT;
        PortalBundleConfiguration bundleConfiguration = initBundleConfiguration();

        if( bundleConfiguration != null )
        {
            httpPort = bundleConfiguration.getHttpPort();
        }

        return httpPort;
    }

    @Override
    public IModule[] getChildModules( IModule[] module )
    {
        IModule[] retval = null;

        if( module != null )
        {
            final IModuleType moduleType = module[0].getModuleType();

            if( module.length == 1 && moduleType != null && SUPPORT_TYPES_LIST.contains( moduleType.getId() ) )
            {
                retval = new IModule[0];
            }
        }

        return retval;
    }

    @Override
    public boolean getDeveloperMode()
    {
        return getAttribute( PROPERTY_DEVELOPER_MODE, PortalServerConstants.DEFAULT_DEVELOPER_MODE );
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
        String[] retval = new String[0];

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
            return new URL( "http://localhost:" + getHttpPort() );
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
        ServerUtil.setServerDefaultName(getServerWorkingCopy());
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

    @Override
    public void saveConfiguration( IProgressMonitor monitor ) throws CoreException
    {
        PortalBundleConfiguration bundleConfiguration = initBundleConfiguration();

        if( bundleConfiguration != null )
        {
            bundleConfiguration.save( monitor );
        }
    }

    public void applyChange( LiferayServerPort port, IProgressMonitor monitor ) throws CoreException
    {
        if( port.getStoreLocation().equals( LiferayServerPort.defayltStoreInServer ) )
        {
            setAttribute( port.getId(), port.getPort() );
        }
        else
        {
            PortalBundleConfiguration bundleConfiguration = initBundleConfiguration();

            if( bundleConfiguration != null )
            {
                bundleConfiguration.applyChange( port );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public List<LiferayServerPort> getLiferayServerPorts()
    {
        List<LiferayServerPort> liferayServerPorts = SetUniqueList.decorate( new ArrayList<LiferayServerPort>() );

        PortalBundleConfiguration bundleConfiguration = initBundleConfiguration();

        if( bundleConfiguration != null )
        {
            liferayServerPorts.addAll( getVMServerPort() );
            liferayServerPorts.add(
                new LiferayServerPort(
                    ATTR_TELNET_PORT, "Telnet", bundleConfiguration.getTelnetPort(), "TCPIP",
                    LiferayServerPort.defayltStoreInProperties ) );
            liferayServerPorts.addAll( bundleConfiguration.getConfiguredServerPorts() );
        }

        return liferayServerPorts;
    }

    private List<LiferayServerPort> getVMServerPort()
    {
        List<LiferayServerPort> serverPorts = new ArrayList<LiferayServerPort>();

        if( getServer() != null )
        {
            serverPorts.add(
                new LiferayServerPort(
                    ATTR_AGENT_PORT, "Bnd Agent", getAgentPort(), "TCPIP", LiferayServerPort.defayltStoreInServer ) );
            serverPorts.add(
                new LiferayServerPort(
                    ATTR_JMX_PORT, "Jmx Client", getJmxPort(), "TCPIP", LiferayServerPort.defayltStoreInServer ) );
        }

        return serverPorts;
    }

    @Override
    public synchronized void importRuntimeConfiguration( IRuntime runtime, IProgressMonitor monitor )
        throws CoreException
    {
        initBundleConfiguration();
    }

    public synchronized PortalBundleConfiguration initBundleConfiguration()
    {
        PortalRuntime portalRuntime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );

        return portalRuntime.getPortalBundle().initBundleConfiguration();
    }

    @Override
    public int getAgentPort()
    {
        return getAttribute( ATTR_AGENT_PORT, ILiferayServer.DEFAULT_AGENT_PORT );
    }

    @Override
    public int getJmxPort()
    {
        return getAttribute( ATTR_JMX_PORT, ILiferayServer.DEFAULT_JMX_PORT );
    }

    @Override
    public int getTelnetPort()
    {
        PortalRuntime _portalRuntime =
            (PortalRuntime) getServer().getRuntime().loadAdapter( PortalRuntime.class, new NullProgressMonitor() );
        return _portalRuntime.getPortalBundle().initBundleConfiguration().getTelnetPort();
    }
}
