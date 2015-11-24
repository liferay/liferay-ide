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
 *******************************************************************************/

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 * @author Gregory Amerson
 */
public class LiferayCore extends Plugin
{
    private static final LiferayProjectAdapterReader adapterReader = new LiferayProjectAdapterReader();

    public static final IPath GLOBAL_SETTINGS_PATH =
        new Path( System.getProperty( "user.home", "" ) + "/.liferay-ide" );

    private static LiferayLanguagePropertiesListener liferayLanguagePropertiesListener;

    // The shared instance
    private static LiferayCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.core";

    private static LiferayProjectProviderReader providerReader;

    public static ILiferayProject create( Object adaptable )
    {
        ILiferayProject project = null;

        if( adaptable != null )
        {
            final ILiferayProjectProvider[] providers = getProviders( adaptable.getClass() );

            if( ! CoreUtil.isNullOrEmpty( providers ) )
            {
                ILiferayProjectProvider currentProvider = null;

                for( ILiferayProjectProvider provider : providers )
                {
                    if ( currentProvider == null || provider.getPriority() > currentProvider.getPriority() )
                    {
                        final ILiferayProject lrp = provider.provide( adaptable );

                        if( lrp != null )
                        {
                            currentProvider = provider;
                            project = lrp;
                        }
                    }
                }
            }
        }

        return project;
    }

    public static <T> T create( Class<T> type, Object adaptable )
    {
        T retval = null;

        if( type != null )
        {
            final ILiferayProject lrproject = create( adaptable );

            if( lrproject != null && type.isAssignableFrom( lrproject.getClass() ) )
            {
                retval = type.cast( lrproject );
            }

            if( retval == null && lrproject != null )
            {
                retval = lrproject.adapt( type );
            }
        }

        return retval;
    }

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
        return createInfoStatus( PLUGIN_ID, msg );
    }

    public static IStatus createInfoStatus( String pluginId, String msg )
    {
        return new Status( IStatus.INFO, pluginId, msg );
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
    public static LiferayCore getDefault()
    {
        return plugin;
    }

    public static synchronized ILiferayProjectAdapter[] getProjectAdapters()
    {
        return adapterReader.getExtensions().toArray( new ILiferayProjectAdapter[0] );
    }

    public static synchronized ILiferayProjectProvider getProvider( String shortName )
    {
        for( ILiferayProjectProvider provider : getProviders() )
        {
            if( provider.getShortName().equals( shortName ) )
            {
                return provider;
            }
        }

        return null;
    }

    public static synchronized ILiferayProjectProvider[] getProviders()
    {
        if( providerReader == null )
        {
            providerReader = new LiferayProjectProviderReader();
        }

        return providerReader.getProviders();
    }

    public static synchronized ILiferayProjectProvider[] getProviders( String projectType )
    {
        if( providerReader == null )
        {
            providerReader = new LiferayProjectProviderReader();
        }

        return providerReader.getProviders( projectType );
    }

    public static synchronized ILiferayProjectProvider[] getProviders( Class<?> type )
    {
        if( providerReader == null )
        {
            providerReader = new LiferayProjectProviderReader();
        }

        return providerReader.getProviders( type );
    }

    public static IProxyService getProxyService()
    {
        final ServiceTracker<Object, Object> proxyTracker =
            new ServiceTracker<Object, Object>(
                getDefault().getBundle().getBundleContext(), IProxyService.class.getName(), null );

        proxyTracker.open();

        final IProxyService proxyService = (IProxyService) proxyTracker.getService();

        proxyTracker.close();

        return proxyService;
    }

    public static void logError( IStatus status )
    {
        getDefault().getLog().log( status );
    }

    public static void logError( String msg )
    {
        logError( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Throwable t )
    {
        getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, t ) );
    }

    public static void logError( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
    }

    public static void logInfo( String msg )
    {
        logError( createInfoStatus( msg ) );
    }

    public static void logWarning( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.WARNING, PLUGIN_ID, t.getMessage(), t ) );
    }

    /**
     * The constructor
     */
    public LiferayCore()
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

        if( liferayLanguagePropertiesListener == null )
        {
            liferayLanguagePropertiesListener = new LiferayLanguagePropertiesListener();

            ResourcesPlugin.getWorkspace().addResourceChangeListener(
                liferayLanguagePropertiesListener, IResourceChangeEvent.POST_CHANGE );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if( liferayLanguagePropertiesListener != null )
        {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener( liferayLanguagePropertiesListener );
            liferayLanguagePropertiesListener = null;
        }
    }

}
