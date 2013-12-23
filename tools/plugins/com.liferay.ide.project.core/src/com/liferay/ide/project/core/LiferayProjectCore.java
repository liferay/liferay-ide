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

package com.liferay.ide.project.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 */
public class LiferayProjectCore extends LiferayCore
{

    // The liferay project marker type
    public static final String LIFERAY_PROJECT_MARKR_TYPE = "com.liferay.ide.project.core.LiferayProjectMarker";

    // The shared instance
    private static LiferayProjectCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.project.core"; //$NON-NLS-1$

    private static PluginPackageResourceListener pluginPackageResourceListener;

    private static IPortletFramework[] portletFrameworks;

    // The key of default project build type for creating a new liferay plug in project
    public static final String PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION = "project-default-build-type-option";

    // The key of using snapshot vresion stored in prefernce store
    public static final String PREF_USE_SNAPSHOT_VERSION = "use-snapshot-version";

    public static final String USE_PROJECT_SETTINGS = "use-project-settings"; //$NON-NLS-1$

    // The value of ant build type
    public static final String VALUE_PROJECT_ANT_BUILD_TYPE = "ant";

    // The value of maven build type
    public static final String VALUE_PROJECT_MAVEN_BUILD_TYPE = "maven";

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static LiferayProjectCore getDefault()
    {
        return plugin;
    }

    public static IPortletFramework getPortletFramework( String name )
    {
        for( IPortletFramework framework : getPortletFrameworks() )
        {
            if( framework.getShortName().equals( name ) )
            {
                return framework;
            }
        }

        return null;
    }

    public static synchronized IPortletFramework[] getPortletFrameworks()
    {
        if( portletFrameworks == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( IPortletFramework.EXTENSION_ID );

            if( !CoreUtil.isNullOrEmpty( elements ) )
            {
                List<IPortletFramework> frameworks = new ArrayList<IPortletFramework>();

                for( IConfigurationElement element : elements )
                {
                    String id = element.getAttribute( IPortletFramework.ID );
                    String shortName = element.getAttribute( IPortletFramework.SHORT_NAME );
                    String displayName = element.getAttribute( IPortletFramework.DISPLAY_NAME );
                    String description = element.getAttribute( IPortletFramework.DESCRIPTION );
                    String requiredSDKVersion =
                        element.getAttribute( IPortletFramework.REQUIRED_SDK_VERSION );

                    boolean isDefault =
                        Boolean.parseBoolean( element.getAttribute( IPortletFramework.DEFAULT ) );

                    boolean isAdvanced =
                        Boolean.parseBoolean( element.getAttribute( IPortletFramework.ADVANCED ) );

                    boolean isRequiresAdvanced =
                        Boolean.parseBoolean( element.getAttribute( IPortletFramework.REQUIRES_ADVANCED ) );

                    URL helpUrl = null;

                    try
                    {
                        helpUrl = new URL( element.getAttribute( IPortletFramework.HELP_URL ) );
                    }
                    catch( Exception e1 )
                    {
                    }

                    try
                    {
                        AbstractPortletFramework framework =
                                (AbstractPortletFramework) element.createExecutableExtension( "class" ); //$NON-NLS-1$
                        framework.setId( id );
                        framework.setShortName( shortName );
                        framework.setDisplayName( displayName );
                        framework.setDescription( description );
                        framework.setRequiredSDKVersion( requiredSDKVersion );
                        framework.setHelpUrl( helpUrl );
                        framework.setDefault( isDefault );
                        framework.setAdvanced( isAdvanced );
                        framework.setRequiresAdvanced( isRequiresAdvanced );
                        framework.setBundleId( element.getContributor().getName() );

                        frameworks.add( framework );
                    }
                    catch( Exception e )
                    {
                        logError( "Could not create portlet framework.", e ); //$NON-NLS-1$
                    }
                }

                portletFrameworks = frameworks.toArray( new IPortletFramework[0] );

                // sort the array so that the default template is first
                Arrays.sort(
                    portletFrameworks, 0, portletFrameworks.length, new Comparator<IPortletFramework>()
                    {

                        public int compare( IPortletFramework o1, IPortletFramework o2 )
                        {
                            if( o1.isDefault() && ( !o2.isDefault() ) )
                            {
                                return -1;
                            }
                            else if( ( !o1.isDefault() ) && o2.isDefault() )
                            {
                                return 1;
                            }

                            return o1.getShortName().compareTo( o2.getShortName() );
                        }

                    } );
            }
        }

        return portletFrameworks;
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, e ) );
    }

    /**
     * The constructor
     */
    public LiferayProjectCore()
    {
        pluginPackageResourceListener = new PluginPackageResourceListener();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        plugin = this;

        ResourcesPlugin.getWorkspace().addResourceChangeListener(
            pluginPackageResourceListener, IResourceChangeEvent.POST_CHANGE );

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;

        super.stop( context );

        if( pluginPackageResourceListener != null )
        {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener( pluginPackageResourceListener );
        }
    }

}
