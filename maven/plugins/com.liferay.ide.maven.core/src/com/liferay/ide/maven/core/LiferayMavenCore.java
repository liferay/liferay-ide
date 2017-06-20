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

import com.liferay.ide.core.util.MultiStatusBuilder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Eric Min
 * @author Terry Jia
 */
public class LiferayMavenCore extends Plugin
{

    // The shared instance
    private static LiferayMavenCore plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.maven.core"; //$NON-NLS-1$

    // set maven project context root with suffix
    public static final String PREF_ADD_MAVEN_PLUGIN_SUFFIX = "add-maven-plugin-suffix";

    public static final String PREF_ARCHETYPE_GAV_EXT = "archetype-gav-ext";
    public static final String PREF_ARCHETYPE_GAV_HOOK = "archetype-gav-hook";
    public static final String PREF_ARCHETYPE_GAV_ICEFACES = "archetype-gav-icefaces";
    public static final String PREF_ARCHETYPE_GAV_JSF = "archetype-gav-jsf";
    public static final String PREF_ARCHETYPE_GAV_LAYOUTTPL = "archetype-gav-layouttpl";
    public static final String PREF_ARCHETYPE_GAV_LIFERAY_FACES_ALLOY = "archetype-gav-liferay-faces-alloy";
    public static final String PREF_ARCHETYPE_GAV_MVC = "archetype-gav-mvc";
    public static final String PREF_ARCHETYPE_GAV_PRIMEFACES = "archetype-gav-primefaces";
    public static final String PREF_ARCHETYPE_GAV_RICHFACES = "archetype-gav-richfaces";
    public static final String PREF_ARCHETYPE_GAV_SERVICEBUILDER = "archetype-gav-servicebuilder";
    public static final String PREF_ARCHETYPE_GAV_SPRING_MVC = "archetype-gav-spring-mvc";
    public static final String PREF_ARCHETYPE_GAV_THEME = "archetype-gav-theme";
    public static final String PREF_ARCHETYPE_GAV_VAADIN = "archetype-gav-vaadin";
    public static final String PREF_ARCHETYPE_GAV_WEB = "archetype-gav-web";

    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_PREFIX = "archetype-project-template-";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_ACTIVATOR = "archetype-project-template-activator";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_API = "archetype-project-template-api";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTENT_TARGETING_REPORT = "archetype-project-template-content-targeting-report";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTENT_TARGETING_RULE = "archetype-project-template-content-targeting-rule";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTENT_TARGETING_TRACKING_ACTION = "archetype-project-template-content-targeting-tracking-action";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_CONTROL_MENU_ENTRY = "archetype-project-template-control-menu-entry";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_FORM_FIELD = "archetype-project-template-form-field";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_FRAGMENT = "archetype-project-template-fragment";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_FREEMERKER_PORTLET = "archetype-project-template-freemarker-portlet";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_LAYOUT_TEMPLATE = "archetype-project-template-layout-template";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_MVC_PORTLET = "archetype-project-template-mvc-portlet";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_PANEL_APP = "archetype-project-template-panel-app";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET = "archetype-project-template-portlet";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET_CONFIGURATION_ICON = "archetype-project-template-portlet-configuration-icon";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET_PROVIDER = "archetype-project-template-portlet-provider";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_PORTLET_TOOLBAR_CONTRIBUTOR = "archetype-project-template-portlet-toolbar-contributor";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_REST = "archetype-project-template-rest";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_SERVICE = "archetype-project-template-service";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_SERVICE_BUILDER = "archetype-project-template-service-builder";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_SERVICE_WRAPPER = "archetype-project-template-service-wrapper";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_SIMULATION_PANEL_ENTRY = "archetype-project-template-simulation-panel-entry";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_SOY_PORTLET = "archetype-project-template-soy-portlet";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_SPRING_MVC_PORTLET = "archetype-project-template-spring-mvc-portlet";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_TEMPLATE_CONTEXT_CONTRIBUTOR = "archetype-project-template-template-context-contributor";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_THEME = "archetype-project-template-theme";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_THEME_CONTRIBUTOR = "archetype-project-template-theme-contributor";
    public static final String PREF_ARCHETYPE_PROJECT_TEMPLATE_WORKSPACE = "archetype-project-template-workspace";

    // The key of disable customJspValidation checking
    public static final String PREF_DISABLE_CUSTOM_JSP_VALIDATION = "disable-custom-jsp-validation";

    private static final IScopeContext[] scopes =
                    new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE };

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

    public static boolean getPreferenceBoolean( String key )
    {
        return Platform.getPreferencesService().getBoolean( PLUGIN_ID, key, false, scopes );
    }

    public static String getPreferenceString( final String key, final String defaultValue )
    {
        return Platform.getPreferencesService().getString( PLUGIN_ID, key, defaultValue, scopes );
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

    public static MultiStatusBuilder newMultiStatus()
    {
        return new MultiStatusBuilder( PLUGIN_ID );
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
