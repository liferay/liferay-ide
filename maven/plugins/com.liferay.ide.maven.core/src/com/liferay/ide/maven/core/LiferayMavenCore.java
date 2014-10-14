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

package com.liferay.ide.maven.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
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
 * @author Simon Jiang
 */
public class LiferayMavenCore extends Plugin
{

    // The shared instance
    private static LiferayMavenCore plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.maven.core"; //$NON-NLS-1$

    // The key of disable customJspValidation checking
    public static final String PREF_DISABLE_CUSTOM_JSP_VALIDATION = "disable-custom-jsp-validation";

    public static final String PREF_MAVEN_MVC_ARCHETYPE = "com.liferay.maven.archetypes:liferay-portlet-archetype";

    public static final String PREF_MAVEN_JSF_ARCHETYPE = "com.liferay.maven.archetypes:liferay-portlet-jsf-archetype";

    public static final String PREF_MAVEN_VAADIN_ARCHETYPE = "com.vaadin:vaadin-archetype-liferay-portlet";

    public static final String PREF_MAVEN_ICEFACES_ARCHETYPE =
        "com.liferay.maven.archetypes:liferay-portlet-icefaces-archetype";

    public static final String PREF_MAVEN_FACES_ALLOY_ARCHETYPE =
        "com.liferay.maven.archetypes:liferay-portlet-liferay-faces-alloy-archetype";

    public static final String PREF_MAVEN_PRIMEFACES_ARCHETYPE =
        "com.liferay.maven.archetypes:liferay-portlet-primefaces-archetype";

    public static final String PREF_MAVEN_RICHFACES_ARCHETYPE =
        "com.liferay.maven.archetypes:liferay-portlet-richfaces-archetype";

    public static final String PREF_MAVEN_HOOK_ARCHETYPE = "com.liferay.maven.archetypes:liferay-hook-archetype";
    
    public static final String PREF_MAVEN_SERVICE_ARCHETYPE = "com.liferay.maven.archetypes:liferay-servicebuilder-archetype";
    
    public static final String PREF_MAVEN_LAYOUTTPL_ARCHETYPE = "com.liferay.maven.archetypes:liferay-layouttpl-archetype";

    public static final String PREF_MAVEN_THEME_ARCHETYPE = "com.liferay.maven.archetypes:liferay-theme-archetype";
    
    public static final String PREF_MAVEN_EXT_ARCHETYPE = "com.liferay.maven.archetypes:liferay-ext-archetype";
    
    public static final String PREF_MAVEN_WEB_ARCHETYPE = "com.liferay.maven.archetypes:liferay-web-archetype";


    public static Status createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, null );
    }

    public static Status createErrorStatus( String msg, Throwable t )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, t );
    }

    public static IStatus createErrorStatus( Throwable throwable )
    {
        return createErrorStatus( throwable.getMessage(), throwable );
    }

    public static IStatus createMultiStatus( int severity, IStatus[] statuses )
    {
        return new MultiStatus(
            LiferayMavenCore.PLUGIN_ID, severity, statuses, statuses[0].getMessage(), statuses[0].getException() );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static LiferayMavenCore getDefault()
    {
        return plugin;
    }

    public static IEclipsePreferences getDefaultPrefs()
    {
        return DefaultScope.INSTANCE.getNode( PLUGIN_ID );
    }

    public static boolean getPreference( String key )
    {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        IPreferencesService preferencesService = Platform.getPreferencesService();

        return preferencesService.getBoolean( PLUGIN_ID, key, false, scopes );
    }

    public static String getPreferenceString( final String key, final String defaultValue )
    {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };
        IPreferencesService preferencesService = Platform.getPreferencesService();

        return preferencesService.getString( PLUGIN_ID, key, defaultValue, scopes );
    }

    public static void log( IStatus status )
    {
        getDefault().getLog().log( status );
    }

    public static void logError( String msg, Throwable t )
    {
        log( createErrorStatus( msg, t ) );
    }

    public static void logError( Throwable t )
    {
        log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
    }

    /**
     * The constructor
     */
    public LiferayMavenCore()
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
