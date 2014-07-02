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

package com.liferay.ide.adt.core;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Gregory Amerson
 */
public class ADTCore extends Plugin
{

    // The shared instance
    private static ADTCore plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.adt.core"; //$NON-NLS-1$

    public static final String PREF_PROJECT_TEMPLATE_LOCATION = "project-template-location";

    public static final String PREF_PROJECT_TEMPLATE_LOCATION_OPTION = "project-template-location-option";

    public static final String VALUE_USE_CUSTOM_TEMPLATE = "use-custom-template";

    public static final String VALUE_USE_EMBEDDED_TEMPLATE = "use-embedded-template";

    private static final IScopeContext[] prefContexts =
                    new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ADTCore getDefault()
    {
        return plugin;
    }

    public static String getEmbeddedProjectTemplateLocation()
    {
        // https://github.com/brunofarache/liferay-mobile-sdk-sample-android
        try
        {
            final URL templateFile = ADTCore.getDefault().getBundle().getEntry( "templates/version.txt" );
            final String latestFile = CoreUtil.readStreamToString( templateFile.openStream() );
            final URL versionUrl = ADTCore.getDefault().getBundle().getEntry( "templates/" + latestFile );

            return new File( FileLocator.toFileURL( versionUrl ).getFile() ).getCanonicalPath();
        }
        catch( IOException e )
        {
            ADTCore.logError( "Could not get built-in project template.", e );
        }

        return null;
    }

    public static File getProjectTemplateFile()
    {
        File retval = null;

        final String templateOption =
            Platform.getPreferencesService().getString(
                PLUGIN_ID, PREF_PROJECT_TEMPLATE_LOCATION_OPTION, ADTCore.VALUE_USE_EMBEDDED_TEMPLATE, prefContexts );

        if( ADTCore.VALUE_USE_CUSTOM_TEMPLATE.equals( templateOption ) )
        {
            final String location =
                Platform.getPreferencesService().getString(
                    PLUGIN_ID, PREF_PROJECT_TEMPLATE_LOCATION, null, prefContexts );

            if( ! CoreUtil.isNullOrEmpty( location ) )
            {
                retval = new File( location );
            }
        }

        if( retval == null || ( ! retval.exists() ) )
        {
            retval = new File( getEmbeddedProjectTemplateLocation() );
        }

        return retval;
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    /**
     * The constructor
     */
    public ADTCore()
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
