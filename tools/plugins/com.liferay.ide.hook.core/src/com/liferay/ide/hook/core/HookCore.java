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

package com.liferay.ide.hook.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class HookCore extends Plugin
{
    // The shared instance
    private static HookCore plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.hook.core"; //$NON-NLS-1$

    public final static String VALIDATOR_ID = "org.eclipse.jst.jsp.core.JSPBatchValidator";

    public static IStatus createErrorStatus( Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createWarnStatus( String msg )
    {
        return new Status( IStatus.WARNING, PLUGIN_ID,msg );
    }
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static HookCore getDefault()
    {
        return plugin;
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    public static void logError( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
    }

    /**
     * The constructor
     */
    public HookCore()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

}
