/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.tomcat.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Greg Amerson
 */
public class LiferayTomcatUIPlugin extends AbstractUIPlugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.server.tomcat.ui"; //$NON-NLS-1$

    // The shared instance
    private static LiferayTomcatUIPlugin plugin;

    public static IStatus createErrorStatus( Exception ex )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex );
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static LiferayTomcatUIPlugin getDefault()
    {
        return plugin;
    }

    public static void logError( Exception ex )
    {
        getDefault().getLog().log( createErrorStatus( ex ) );
    }

    /**
     * The constructor
     */
    public LiferayTomcatUIPlugin()
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
