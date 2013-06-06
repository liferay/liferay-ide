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

package com.liferay.ide.debug.core;

import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregorg Amerson
 */
public class LiferayDebugCore extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.debug.core"; //$NON-NLS-1$

    public static final String ID_FM_BREAKPOINT_TYPE = "com.liferay.ide.debug.core.fmLineBreakpointMarker";

    // The shared instance
    private static LiferayDebugCore plugin;

    public static IStatus createErrorStatus( String msg )
    {
        return LiferayCore.createErrorStatus( PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( Throwable t )
    {
        return LiferayCore.createErrorStatus( PLUGIN_ID, t );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static LiferayDebugCore getDefault()
    {
        return plugin;
    }

    public static void logError( Exception e )
    {
        logError( e.getMessage(), e );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, msg, e ) );
    }

    /**
     * The constructor
     */
    public LiferayDebugCore()
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
