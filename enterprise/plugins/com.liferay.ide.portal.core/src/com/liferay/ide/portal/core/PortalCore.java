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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class PortalCore extends Plugin
{

    public static final String ID_FM_BREAKPOINT_TYPE = "com.liferay.ide.debug.core.fmLineBreakpointMarker"; //$NON-NLS-1$

    // The shared instance
    private static PortalCore plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.portal.core"; //$NON-NLS-1$
    public static final String PREF_ADVANCED_VARIABLES_VIEW = "advanced-variables-view"; //$NON-NLS-1$
    public static final String PREF_FM_DEBUG_PASSWORD = "fm-debug-password"; //$NON-NLS-1$
    public static final String PREF_FM_DEBUG_PORT = "fm-debug-port"; //$NON-NLS-1$

    public static IStatus createErrorStatus( String msg )
    {
        return createErrorStatus( msg, null );
    }

    public static IStatus createErrorStatus( String msg, Throwable e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createErrorStatus( Throwable t )
    {
        return createErrorStatus( t.getMessage(), t );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static PortalCore getDefault()
    {
        return plugin;
    }

    public static IEclipsePreferences getDefaultPrefs()
    {
        return DefaultScope.INSTANCE.getNode( PLUGIN_ID );
    }

    public static String getPreference( String key )
    {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        IPreferencesService preferencesService = Platform.getPreferencesService();

        return preferencesService.getString( PLUGIN_ID, key, null, scopes );
    }

    public static IEclipsePreferences getPrefs()
    {
        return InstanceScope.INSTANCE.getNode( PLUGIN_ID );
    }

    public static void logError( String msg )
    {
        logError( msg, null );
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
    public PortalCore()
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
