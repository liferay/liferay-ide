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

package com.liferay.ide.server.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 * @author Simon Jiang
 */
public class LiferayServerCore extends Plugin
{

    private static Map<String, IServerManagerConnection> connections = null;

    // The shared instance
    private static LiferayServerCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.server.core"; //$NON-NLS-1$

    private static IPluginPublisher[] pluginPublishers = null;

    private static IRuntimeDelegateValidator[] runtimeDelegateValidators;

    private static ILiferayRuntimeStub[] runtimeStubs;

    public static IStatus createErrorStatus( Exception e )
    {
        return createErrorStatus( PLUGIN_ID, e );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return createErrorStatus( PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( String pluginId, String msg )
    {
        return new Status( IStatus.ERROR, pluginId, msg );
    }

    public static IStatus createErrorStatus( String pluginId, String msg, Throwable e )
    {
        return new Status( IStatus.ERROR, pluginId, msg, e );
    }

    public static IStatus createErrorStatus( String pluginId, Throwable t )
    {
        return new Status( IStatus.ERROR, pluginId, t.getMessage(), t );
    }

    public static IStatus createInfoStatus( String msg )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, msg );
    }

    public static IStatus createWarningStatus( String message )
    {
        return new Status( IStatus.WARNING, PLUGIN_ID, message );
    }

    public static IStatus createWarningStatus( String message, String id )
    {
        return new Status( IStatus.WARNING, id, message );
    }

    public static IStatus createWarningStatus( String message, String id, Exception e )
    {
        return new Status( IStatus.WARNING, id, message, e );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static LiferayServerCore getDefault()
    {
        return plugin;
    }

    public static URL getPluginEntry( String path )
    {
        return getDefault().getBundle().getEntry( path );
    }

    public static IPluginPublisher getPluginPublisher( String facetId, String runtimeTypeId )
    {
        if( CoreUtil.isNullOrEmpty( facetId ) || CoreUtil.isNullOrEmpty( runtimeTypeId ) )
        {
            return null;
        }

        IPluginPublisher retval = null;
        IPluginPublisher[] publishers = getPluginPublishers();

        if( publishers != null && publishers.length > 0 )
        {
            for( IPluginPublisher publisher : publishers )
            {
                if( publisher != null && facetId.equals( publisher.getFacetId() ) &&
                    runtimeTypeId.equals( publisher.getRuntimeTypeId() ) )
                {
                    retval = publisher;
                    break;
                }
            }
        }

        return retval;
    }

    public static IPluginPublisher[] getPluginPublishers()
    {
        if( pluginPublishers == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( IPluginPublisher.ID );

            try
            {
                List<IPluginPublisher> deployers = new ArrayList<IPluginPublisher>();

                for( IConfigurationElement element : elements )
                {
                    final Object o = element.createExecutableExtension( "class" ); //$NON-NLS-1$

                    if( o instanceof AbstractPluginPublisher )
                    {
                        AbstractPluginPublisher pluginDeployer = (AbstractPluginPublisher) o;
                        pluginDeployer.setFacetId( element.getAttribute( "facetId" ) ); //$NON-NLS-1$
                        pluginDeployer.setRuntimeTypeId( element.getAttribute( "runtimeTypeId" ) ); //$NON-NLS-1$
                        deployers.add( pluginDeployer );
                    }
                }

                pluginPublishers = deployers.toArray( new IPluginPublisher[0] );
            }
            catch( Exception e )
            {
                logError( "Unable to get plugin deployer extensions", e ); //$NON-NLS-1$
            }
        }

        return pluginPublishers;
    }

    public static URL getPortalSupportLibURL()
    {
        try
        {
            return FileLocator.toFileURL( LiferayServerCore.getPluginEntry( "/portal-support/portal-support.jar" ) ); //$NON-NLS-1$
        }
        catch( IOException e )
        {
        }

        return null;
    }

    public static IServerManagerConnection getRemoteConnection( final IRemoteServer server )
    {
        if( connections == null )
        {
            connections = new HashMap<String, IServerManagerConnection>();

            ServerCore.addServerLifecycleListener( new IServerLifecycleListener()
            {

                public void serverAdded( IServer server )
                {
                }

                public void serverChanged( IServer server )
                {
                }

                public void serverRemoved( IServer s )
                {
                    if( server.equals( s ) )
                    {
                        IServerManagerConnection service = connections.get( server.getId() );

                        if( service != null )
                        {
                            service = null;
                            connections.put( server.getId(), null );
                        }
                    }
                }
            } );
        }

        IServerManagerConnection service = connections.get( server.getId() );

        if( service == null )
        {
            service = new ServerManagerConnection();

            updateConnectionSettings( server, service );

            connections.put( server.getId(), service );
        }
        else
        {
            updateConnectionSettings( server, service );
        }

        return service;
    }

    public static IRuntimeDelegateValidator[] getRuntimeDelegateValidators()
    {
        if( runtimeDelegateValidators == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( IRuntimeDelegateValidator.ID );

            try
            {
                List<IRuntimeDelegateValidator> validators = new ArrayList<IRuntimeDelegateValidator>();

                for( IConfigurationElement element : elements )
                {
                    final Object o = element.createExecutableExtension( "class" ); //$NON-NLS-1$
                    final String runtimeTypeId = element.getAttribute( "runtimeTypeId" ); //$NON-NLS-1$

                    if( o instanceof AbstractRuntimeDelegateValidator )
                    {
                        AbstractRuntimeDelegateValidator validator = (AbstractRuntimeDelegateValidator) o;
                        validator.setRuntimeTypeId( runtimeTypeId );
                        validators.add( validator );
                    }
                }

                runtimeDelegateValidators = validators.toArray( new IRuntimeDelegateValidator[0] );
            }
            catch( Exception e )
            {
                logError( "Unable to get IRuntimeDelegateValidator extensions", e ); //$NON-NLS-1$
            }
        }

        return runtimeDelegateValidators;
    }

    public static ILiferayRuntimeStub getRuntimeStub( String stubTypeId )
    {
        ILiferayRuntimeStub retval = null;

        ILiferayRuntimeStub[] stubs = getRuntimeStubs();

        if( !CoreUtil.isNullOrEmpty( stubs ) )
        {
            for( ILiferayRuntimeStub stub : stubs )
            {
                if( stub.getRuntimeStubTypeId().equals( stubTypeId ) )
                {
                    retval = stub;
                    break;
                }
            }
        }

        return retval;
    }

    public static ILiferayRuntimeStub[] getRuntimeStubs()
    {
        if( runtimeStubs == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( ILiferayRuntimeStub.EXTENSION_ID );

            if( !CoreUtil.isNullOrEmpty( elements ) )
            {
                List<ILiferayRuntimeStub> stubs = new ArrayList<ILiferayRuntimeStub>();

                for( IConfigurationElement element : elements )
                {
                    String runtimeTypeId = element.getAttribute( ILiferayRuntimeStub.RUNTIME_TYPE_ID );
                    String name = element.getAttribute( ILiferayRuntimeStub.NAME );
                    boolean isDefault = Boolean.parseBoolean( element.getAttribute( ILiferayRuntimeStub.DEFAULT ) );

                    try
                    {
                        LiferayRuntimeStub stub = new LiferayRuntimeStub();
                        stub.setRuntimeTypeId( runtimeTypeId );
                        stub.setName( name );
                        stub.setDefault( isDefault );

                        stubs.add( stub );
                    }
                    catch( Exception e )
                    {
                        logError( "Could not create liferay runtime stub.", e ); //$NON-NLS-1$
                    }
                }

                runtimeStubs = stubs.toArray( new ILiferayRuntimeStub[0] );
            }
        }

        return runtimeStubs;
    }

    public static IPath getTempLocation( String prefix, String fileName )
    {
        return getDefault().getStateLocation().append( "tmp" ).append( //$NON-NLS-1$
            prefix + "/" + System.currentTimeMillis() + ( CoreUtil.isNullOrEmpty( fileName ) ? StringPool.EMPTY : "/" + fileName ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( IStatus status )
    {
        getDefault().getLog().log( status );
    }

    public static void logError( String msg )
    {
        logError( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Throwable e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, msg, e ) );
    }

    public static void logError( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
    }

    public static void updateConnectionSettings( IRemoteServer server )
    {
        updateConnectionSettings( server, getRemoteConnection( server ) );
    }

    public static void updateConnectionSettings( IRemoteServer server, IServerManagerConnection remoteConnection )
    {
        remoteConnection.setHost( server.getHost() );
        remoteConnection.setHttpPort( server.getHTTPPort() );
        remoteConnection.setManagerContextPath( server.getServerManagerContextPath() );
        remoteConnection.setUsername( server.getUsername() );
        remoteConnection.setPassword( server.getPassword() );
    }

    public static IStatus validateRuntimeDelegate( RuntimeDelegate runtimeDelegate )
    {
        if( runtimeDelegate.getRuntime().isStub() )
        {
            return Status.OK_STATUS;
        }

        String runtimeTypeId = runtimeDelegate.getRuntime().getRuntimeType().getId();

        IRuntimeDelegateValidator[] validators = getRuntimeDelegateValidators();

        if( !CoreUtil.isNullOrEmpty( validators ) )
        {
            for( IRuntimeDelegateValidator validator : validators )
            {
                if( runtimeTypeId.equals( validator.getRuntimeTypeId() ) )
                {
                    IStatus status = validator.validateRuntimeDelegate( runtimeDelegate );

                    if( !status.isOK() )
                    {
                        return status;
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * The constructor
     */
    public LiferayServerCore()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        plugin = this;

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;

        super.stop( context );

    }
}
