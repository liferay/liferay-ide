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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.descriptor.IDescriptorOperation;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.modules.IComponentTemplate;
import com.liferay.ide.project.core.modules.LiferayComponentTemplateReader;
import com.liferay.ide.project.core.upgrade.ILiferayLegacyProjectUpdater;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings( "rawtypes" )
public class ProjectCore extends Plugin
{
    // The liferay project marker type
    public static final String LIFERAY_PROJECT_MARKER_TYPE = "com.liferay.ide.project.core.LiferayProjectMarker";

    // The shared instance
    private static ProjectCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.project.core"; //$NON-NLS-1$

    private static PluginPackageResourceListener pluginPackageResourceListener;

    private static SDKBuildPropertiesResourceListener sdkBuildPropertiesResourceListener;

    private static SDKProjectDeleteListener sdkProjectDeleteListener;

    private static IPortletFramework[] portletFrameworks;

    private static ServiceTracker<ILiferayLegacyProjectUpdater, ILiferayLegacyProjectUpdater> _liferayLegacyProjectUpdaterTracker;

    public static final String PREF_CREATE_NEW_PORLET = "create-new-portlet";

    // The key of default project build type for creating a new liferay plug in project
    public static final String PREF_DEFAULT_PLUGIN_PROJECT_BUILD_TYPE_OPTION = "project-plugin_default-build-type-option";

    public static final String PREF_DEFAULT_MODULE_PROJECT_BUILD_TYPE_OPTION = "project-module-default-build-type-option";

    public static final String PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION =
        "project-module-fragment-default-build-type-option";

    public static final String PREF_DEFAULT_WORKSPACE_PROJECT_BUILD_TYPE_OPTION = "project-workspace-default-build-type-option";

    public static final String PREF_DEFAULT_PLUGIN_PROJECT_MAVEN_GROUPID = "default-plugin-project-maven-groupid";
    public static final String PREF_DEFAULT_MODULE_PROJECT_MAVEN_GROUPID = "default-module-project-maven-groupid";

    public static final String PREF_INCLUDE_SAMPLE_CODE = "include-sample-code";

    public static final String USE_PROJECT_SETTINGS = "use-project-settings"; //$NON-NLS-1$

    private static LiferayComponentTemplateReader componentTemplateReader;

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
    public static ProjectCore getDefault()
    {
        return plugin;
    }

    private static LiferayDescriptorHelper[] getDescriptorHelpers( IProject project, Class<? extends IDescriptorOperation> type )
    {
        List<LiferayDescriptorHelper> retval = new ArrayList<LiferayDescriptorHelper>();

        project = CoreUtil.getLiferayProject( project );

        if( project == null || ! project.exists() )
        {
            return null;
        }

        final LiferayDescriptorHelper[] allHelpers = LiferayDescriptorHelperReader.getInstance().getAllHelpers();

        for( LiferayDescriptorHelper helper : allHelpers )
        {
            helper.setProject( project );

            final IFile descriptorFile = helper.getDescriptorFile();

            if( descriptorFile != null && descriptorFile.exists() )
            {
                if( helper.getDescriptorOperation( type ) != null )
                {
                    retval.add( helper );
                }
            }
        }

        return retval.toArray( new LiferayDescriptorHelper[0] );
    }

    public ILiferayLegacyProjectUpdater getLiferayLegacyProjectUpdater()
    {
        return _liferayLegacyProjectUpdaterTracker.getService();
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

                        @Override
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

    public static void logError( IStatus status )
    {
        getDefault().getLog().log( status );
    }

    public static void logError( String msg )
    {
        logError( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, e ) );
    }

    public static void logError( String msg, Throwable t )
    {
        getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, t ) );
    }

    public static void logError( Throwable t )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, t.getMessage(), t ) );
    }

    public static IStatus operate( IProject project, Class<? extends IDescriptorOperation> type, Object... params )
    {
        IStatus status = Status.OK_STATUS;

        LiferayDescriptorHelper[] helpers = getDescriptorHelpers( project, type );

        for( LiferayDescriptorHelper helper : helpers )
        {
            status = helper.getDescriptorOperation( type ).execute( params );

            if( ! status.isOK() )
            {
                return status;
            }
        }

        return status;
    }

    /**
     * The constructor
     */
    public ProjectCore()
    {
        pluginPackageResourceListener = new PluginPackageResourceListener();
        sdkBuildPropertiesResourceListener = new SDKBuildPropertiesResourceListener();
        sdkProjectDeleteListener = new SDKProjectDeleteListener();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        plugin = this;

        _liferayLegacyProjectUpdaterTracker =
            new ServiceTracker<ILiferayLegacyProjectUpdater, ILiferayLegacyProjectUpdater>(
                context, ILiferayLegacyProjectUpdater.class, null );
        _liferayLegacyProjectUpdaterTracker.open();

        CoreUtil.getWorkspace().addResourceChangeListener(
            pluginPackageResourceListener, IResourceChangeEvent.POST_CHANGE );

        CoreUtil.getWorkspace().addResourceChangeListener(
            sdkBuildPropertiesResourceListener, IResourceChangeEvent.POST_CHANGE );

        CoreUtil.getWorkspace().addResourceChangeListener(
            sdkProjectDeleteListener , IResourceChangeEvent.PRE_DELETE );

        /*
        final Job job = new Job( "Checking for the latest Blade CLI" )
        {
            @Override
            public IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    BladeCLI.getBladeCLIPath();
                }
                catch( BladeCLIException e )
                {
                    // ignore any errors
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
        */
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;

        super.stop( context );

        if( pluginPackageResourceListener != null )
        {
            CoreUtil.getWorkspace().removeResourceChangeListener( pluginPackageResourceListener );
        }

        if( sdkBuildPropertiesResourceListener != null )
        {
            CoreUtil.getWorkspace().removeResourceChangeListener( sdkBuildPropertiesResourceListener );
        }

        if( sdkProjectDeleteListener != null )
        {
            CoreUtil.getWorkspace().removeResourceChangeListener( sdkProjectDeleteListener );
        }
    }

    public static IComponentTemplate getComponentTemplate( final String templateName )
    {
        for( IComponentTemplate template : getComponentTemplates() )
        {
            if( templateName.equals( template.getShortName() ) )
            {
                return template;
            }
        }

        return null;
    }

    public static IComponentTemplate[] getComponentTemplates()
    {
        if( componentTemplateReader == null )
        {
            componentTemplateReader = new LiferayComponentTemplateReader();
        }

        return componentTemplateReader.getComponentTemplates();
    }
}
