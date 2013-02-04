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
 *******************************************************************************/

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Gregory Amerson
 */
public class LiferayCore extends Plugin
{
    // The shared instance
    private static LiferayCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.core"; //$NON-NLS-1$

    private static LiferayProjectProviderReader reader;

    public static ILiferayProject create( Object adaptable )
    {
        ILiferayProject retval = null;

        final ILiferayProjectProvider[] providers = getProviders( adaptable.getClass() );

        if( ! CoreUtil.isNullOrEmpty( providers ) )
        {
            for( ILiferayProjectProvider provider : providers )
            {
                final ILiferayProject lProject = provider.provide( adaptable );

                if( lProject != null )
                {
                    retval = lProject;
                    break;
                }
            }
        }

        if( retval == null )
        {
            LiferayCore.logError( "No liferay project providers registered for type: " + adaptable.getClass() ); //$NON-NLS-1$
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

    public static synchronized ILiferayProjectProvider[] getProviders( Class<?> type )
    {
        if( reader == null )
        {
            reader = new LiferayProjectProviderReader();
        }

        return reader.getProviders( type );
    }

    public static void logError( String msg )
    {
        getDefault().getLog().log( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Throwable t )
    {
        getDefault().getLog().log( createErrorStatus( msg, t ) );
    }

    public static void logError( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
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
