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

package com.liferay.ide.project.core;

import com.liferay.ide.core.CorePlugin;
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
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Greg Amerson
 */
public class ProjectCorePlugin extends CorePlugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.project.core"; //$NON-NLS-1$

    public static final String USE_PROJECT_SETTINGS = "use-project-settings"; //$NON-NLS-1$

    // The shared instance
    private static ProjectCorePlugin plugin;

    private static PluginPackageResourceListener pluginPackageResourceListener;

    private static IPortletFrameworkWizardProvider[] portletFrameworks;

    private static IProjectDefinition[] projectDefinitions = null;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ProjectCorePlugin getDefault()
    {
        return plugin;
    }

    public static IPortletFrameworkWizardProvider getPortletFramework( String id )
    {
        for( IPortletFrameworkWizardProvider framework : getPortletFrameworks() )
        {
            if( framework.getId().equals( id ) )
            {
                return framework;
            }
        }

        return null;
    }

    public static IPortletFrameworkWizardProvider[] getPortletFrameworks()
    {
        return getPortletFrameworks( false );
    }

    public static IPortletFrameworkWizardProvider[] getPortletFrameworks( boolean reinitialize )
    {
        if( portletFrameworks == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor(
                    IPortletFrameworkWizardProvider.EXTENSION_ID );

            if( !CoreUtil.isNullOrEmpty( elements ) )
            {
                List<IPortletFrameworkWizardProvider> frameworks = new ArrayList<IPortletFrameworkWizardProvider>();

                for( IConfigurationElement element : elements )
                {
                    String id = element.getAttribute( IPortletFrameworkWizardProvider.ID );
                    String shortName = element.getAttribute( IPortletFrameworkWizardProvider.SHORT_NAME );
                    String displayName = element.getAttribute( IPortletFrameworkWizardProvider.DISPLAY_NAME );
                    String description = element.getAttribute( IPortletFrameworkWizardProvider.DESCRIPTION );
                    String requiredSDKVersion =
                        element.getAttribute( IPortletFrameworkWizardProvider.REQUIRED_SDK_VERSION );
                    boolean isDefault =
                        Boolean.parseBoolean( element.getAttribute( IPortletFrameworkWizardProvider.DEFAULT ) );

                    URL helpUrl = null;

                    try
                    {
                        helpUrl = new URL( element.getAttribute( IPortletFrameworkWizardProvider.HELP_URL ) );
                    }
                    catch( Exception e1 )
                    {
                    }

                    try
                    {
                        AbstractPortletFrameworkWizardProvider framework =
                            (AbstractPortletFrameworkWizardProvider) element.createExecutableExtension( "class" ); //$NON-NLS-1$
                        framework.setId( id );
                        framework.setShortName( shortName );
                        framework.setDisplayName( displayName );
                        framework.setDescription( description );
                        framework.setRequiredSDKVersion( requiredSDKVersion );
                        framework.setHelpUrl( helpUrl );
                        framework.setDefault( isDefault );
                        framework.setBundleId( element.getContributor().getName() );

                        frameworks.add( framework );
                    }
                    catch( Exception e )
                    {
                        logError( "Could not create portlet framework.", e ); //$NON-NLS-1$
                    }
                }

                portletFrameworks = frameworks.toArray( new IPortletFrameworkWizardProvider[0] );

                // sort the array so that the default template is first
                Arrays.sort(
                    portletFrameworks, 0, portletFrameworks.length, new Comparator<IPortletFrameworkWizardProvider>()
                    {

                        public int compare( IPortletFrameworkWizardProvider o1, IPortletFrameworkWizardProvider o2 )
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

        if( reinitialize )
        {
            for( IPortletFrameworkWizardProvider portletFramework : portletFrameworks )
            {
                portletFramework.reinitialize();
            }
        }

        return portletFrameworks;
    }

    public static IProjectDefinition getProjectDefinition( IProjectFacet projectFacet )
    {
        IProjectDefinition[] definitions = getProjectDefinitions();

        if( definitions != null )
        {
            for( IProjectDefinition def : definitions )
            {
                if( def != null && def.getFacet() != null && def.getFacet().equals( projectFacet ) )
                {
                    return def;
                }
            }
        }

        return null;
    }

    public static IProjectDefinition getProjectDefinition( String type )
    {
        IProjectDefinition[] defs = getProjectDefinitions();

        if( defs != null && defs.length > 0 )
        {
            for( IProjectDefinition def : defs )
            {
                if( def != null && def.getFacetId().equals( type ) )
                {
                    return def;
                }
            }
        }

        return null;
    }

    public static IProjectDefinition[] getProjectDefinitions()
    {
        if( projectDefinitions == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( IProjectDefinition.ID );

            try
            {
                List<IProjectDefinition> definitions = new ArrayList<IProjectDefinition>();

                for( IConfigurationElement element : elements )
                {
                    final Object o = element.createExecutableExtension( "class" ); //$NON-NLS-1$

                    if( o instanceof AbstractProjectDefinition )
                    {
                        AbstractProjectDefinition projectDefinition = (AbstractProjectDefinition) o;
                        projectDefinition.setFacetId( element.getAttribute( "facetId" ) ); //$NON-NLS-1$
                        projectDefinition.setShortName( element.getAttribute( "shortName" ) ); //$NON-NLS-1$
                        projectDefinition.setDisplayName( element.getAttribute( "displayName" ) ); //$NON-NLS-1$
                        projectDefinition.setFacetedProjectTemplateId( element.getAttribute( "facetedProjectTemplateId" ) ); //$NON-NLS-1$

                        int menuIndex = Integer.MAX_VALUE;

                        try
                        {
                            String intVal = element.getAttribute( "menuIndex" ); //$NON-NLS-1$

                            if( intVal != null )
                            {
                                menuIndex = Integer.parseInt( intVal );
                            }
                        }
                        catch( Exception e )
                        {
                            ProjectCorePlugin.logError( "Error reading project definition.", e ); //$NON-NLS-1$
                        }

                        projectDefinition.setMenuIndex( menuIndex );

                        definitions.add( projectDefinition );
                    }
                }

                projectDefinitions = definitions.toArray( new IProjectDefinition[0] );
            }
            catch( Exception e )
            {
                logError( e );
            }
        }

        return projectDefinitions;
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    /**
     * The constructor
     */
    public ProjectCorePlugin()
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
