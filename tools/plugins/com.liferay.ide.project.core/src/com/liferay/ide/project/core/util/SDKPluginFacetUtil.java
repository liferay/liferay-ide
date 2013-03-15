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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author Greg Amerson
 */
public class SDKPluginFacetUtil
{

    public static final IProjectFacet[] LIFERAY_FACETS = new IProjectFacet[]
    {
        IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET,
        IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET,
        IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET,
        IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET,
        IPluginFacetConstants.LIFERAY_THEME_PROJECT_FACET
    };

    public static void configureJavaFacet( IFacetedProjectWorkingCopy fpjwc, IProjectFacet requiredFacet, IPreset preset )
    {
        Action action = fpjwc.getProjectFacetAction( requiredFacet );

        if( action == null )
        {
            return;
        }

        Object config = action.getConfig();

        if( !( config instanceof JavaFacetInstallConfig ) )
        {
            return;
        }

        JavaFacetInstallConfig javaConfig = (JavaFacetInstallConfig) config;

        IDataModel dm = (IDataModel) Platform.getAdapterManager().getAdapter( config, IDataModel.class );
        String presetId = preset.getId();

        if( presetId.contains( "portlet" ) ) //$NON-NLS-1$
        {
            javaConfig.setSourceFolder( new Path( IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER ) );
            javaConfig.setDefaultOutputFolder( new Path( IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER ) );

            dm.setStringProperty(
                IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
                IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER );

            dm.setStringProperty(
                IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
                IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER );
        }
        else if( presetId.contains( "hook" ) ) //$NON-NLS-1$
        {
            javaConfig.setSourceFolder( new Path( IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER ) );
            javaConfig.setDefaultOutputFolder( new Path( IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER ) );

            dm.setStringProperty(
                IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
                IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER );

            dm.setStringProperty(
                IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
                IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER );
        }
        else if( presetId.contains( "layouttpl" ) || presetId.contains( "theme" ) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            dm.setStringProperty( IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME, null );
            dm.setStringProperty(
                IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
                IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER );

            List<IPath> srcFolders = javaConfig.getSourceFolders();

            if( !CoreUtil.isNullOrEmpty( srcFolders ) )
            {
                for( IPath srcFolder : srcFolders )
                {
                    javaConfig.removeSourceFolder( srcFolder );
                }
            }
        }
    }

    public static void configureLiferayFacet(
        IFacetedProjectWorkingCopy fpjwc, IProjectFacet requiredFacet, String sdkLocation )
    {
        Action action = fpjwc.getProjectFacetAction( requiredFacet );

        if( action != null )
        {
            Object config = action.getConfig();
            IDataModel dm = (IDataModel) config;
            dm.setProperty( IPluginProjectDataModelProperties.LIFERAY_SDK_NAME, getSDKName( sdkLocation ) );
        }
    }

    public static void configureLiferayFacet(
        IFacetedProjectWorkingCopy fpjwc, IProjectFacetVersion requiredFacetVersion, String sdkLocation )
    {
        configureLiferayFacet( fpjwc, requiredFacetVersion.getProjectFacet(), sdkLocation );
    }

    public static void configureProjectAsPlugin( IFacetedProjectWorkingCopy fpjwc, IRuntime runtime, String sdkLocation )
        throws CoreException
    {
        // final IPreset preset = template.getInitialPreset();
        // final IRuntime runtime = (IRuntime)
        // model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);

        fpjwc.setTargetedRuntimes( Collections.<IRuntime> emptySet() );

        if( runtime != null )
        {
            // final Set<IProjectFacetVersion> minFacets = new
            // HashSet<IProjectFacetVersion>();
            //
            // try {
            // for( IProjectFacet f : fpjwc.getFixedProjectFacets() ) {
            // minFacets.add( f.getLatestSupportedVersion( runtime ) );
            // }
            // }
            // catch( CoreException e ) {
            // throw new RuntimeException( e );
            // }
            //
            // fpjwc.setProjectFacets( minFacets );

            fpjwc.setTargetedRuntimes( Collections.singleton( runtime ) );
        }

        fpjwc.setPrimaryRuntime( runtime );

        // fpjwc.setSelectedPreset(
        // FacetedProjectFramework.DEFAULT_CONFIGURATION_PRESET_ID );

        IFacetedProjectTemplate template = getLiferayTemplateForProject( fpjwc );
        IPreset preset = getLiferayPresetForProject( fpjwc );

        if( preset == null )
        {
            throw new CoreException( LiferayProjectCore.createErrorStatus( NLS.bind( Msgs.noFacetPreset, fpjwc.getProjectName() ) ) );
        }

        Set<IProjectFacetVersion> currentProjectFacetVersions = fpjwc.getProjectFacets();

        Set<IProjectFacet> requiredFacets = template.getFixedProjectFacets();

        for( IProjectFacet requiredFacet : requiredFacets )
        {
            boolean hasRequiredFacet = false;

            for( IProjectFacetVersion currentFacetVersion : currentProjectFacetVersions )
            {
                if( currentFacetVersion.getProjectFacet().equals( requiredFacet ) )
                {
                    boolean supports = runtime.supports( currentFacetVersion );

                    if( supports )
                    {
                        hasRequiredFacet = true;
                    }
                    else
                    {
                        fpjwc.removeProjectFacet( currentFacetVersion );
                    }

                    break;
                }
            }

            if( !hasRequiredFacet )
            {
                IProjectFacetVersion requiredFacetVersion = getRequiredFacetVersionFromPreset( requiredFacet, preset );

                if( requiredFacetVersion != null )
                {
                    fpjwc.addProjectFacet( requiredFacetVersion );

                    if( ProjectUtil.isJavaFacet( requiredFacetVersion ) )
                    {
                        configureJavaFacet( fpjwc, requiredFacetVersion.getProjectFacet(), preset );
                    }
                    else if( ProjectUtil.isLiferayFacet( requiredFacetVersion ) )
                    {
                        configureLiferayFacet( fpjwc, requiredFacetVersion, sdkLocation );
                    }
                    else if( ProjectUtil.isDynamicWebFacet( requiredFacetVersion ) )
                    {
                        configureWebFacet( fpjwc, requiredFacetVersion.getProjectFacet(), preset );
                    }
                }
            }
            else
            {
                if( ProjectUtil.isJavaFacet( requiredFacet ) )
                {
                    configureJavaFacet( fpjwc, requiredFacet, preset );
                }
                else if( ProjectUtil.isLiferayFacet( requiredFacet ) )
                {
                    configureLiferayFacet( fpjwc, requiredFacet, sdkLocation );
                }
            }
        }
    }

    private static IProjectFacetVersion getRequiredFacetVersionFromPreset( IProjectFacet requiredFacet, IPreset preset )
    {
        Set<IProjectFacetVersion> facets = preset.getProjectFacets();

        for( IProjectFacetVersion facet : facets )
        {
            if( facet.getProjectFacet().equals( requiredFacet ) )
            {
                return facet;
            }
        }

        return null;
    }

    public static void configureWebFacet( IFacetedProjectWorkingCopy fpjwc, IProjectFacet requiredFacet, IPreset preset )
        throws CoreException
    {
        Action action = fpjwc.getProjectFacetAction( requiredFacet );
        Object config = action.getConfig();

        if( config instanceof IDataModel )
        {
            IDataModel dm = (IDataModel) config;

            if( preset.getId().contains( "portlet" ) ) //$NON-NLS-1$
            {
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
                    IPluginFacetConstants.PORTLET_PLUGIN_SDK_CONFIG_FOLDER );
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
                    IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER );

                addDefaultWebXml( fpjwc, dm );
            }
            else if( preset.getId().contains( "hook" ) ) //$NON-NLS-1$
            {
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
                    IPluginFacetConstants.HOOK_PLUGIN_SDK_CONFIG_FOLDER );
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.SOURCE_FOLDER,
                    IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER );

                addDefaultWebXml( fpjwc, dm );
            }
            else if( preset.getId().contains( "ext" ) ) //$NON-NLS-1$
            {
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
                    IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER );

                addDefaultWebXml( fpjwc, dm );
            }
            else if( preset.getId().contains( "layouttpl" ) ) //$NON-NLS-1$
            {
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
                    IPluginFacetConstants.LAYOUTTPL_PLUGIN_SDK_CONFIG_FOLDER );
                ProjectUtil.setGenerateDD( dm, false );
            }
            else if( preset.getId().contains( "theme" ) ) //$NON-NLS-1$
            {
                dm.setStringProperty(
                    IWebFacetInstallDataModelProperties.CONFIG_FOLDER,
                    IPluginFacetConstants.THEME_PLUGIN_SDK_CONFIG_FOLDER );
                ProjectUtil.setGenerateDD( dm, false );
            }

        }
    }

    public static IPreset getLiferayPresetForProject( IFacetedProjectWorkingCopy fpjwc )
    {
        IPreset preset = null;
        String projName = fpjwc.getProjectName();
        IPath location = fpjwc.getProjectLocation();

        String directoryName = StringPool.EMPTY;

        if( location != null )
        {
            directoryName = location.lastSegment();
        }

        if( projName.endsWith( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) )
        {
            preset = ProjectFacetsManager.getPreset( IPluginFacetConstants.LIFERAY_PORTLET_PRESET );
        }
        else if( projName.endsWith( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) )
        {
            preset = ProjectFacetsManager.getPreset( IPluginFacetConstants.LIFERAY_HOOK_PRESET );
        }
        else if( projName.endsWith( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) )
        {
            preset = ProjectFacetsManager.getPreset( IPluginFacetConstants.LIFERAY_EXT_PRESET );
        }
        else if( projName.endsWith( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) )
        {
            preset = ProjectFacetsManager.getPreset( IPluginFacetConstants.LIFERAY_LAYOUTTPL_PRESET );
        }
        else if( projName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) )
        {
            preset = ProjectFacetsManager.getPreset( IPluginFacetConstants.LIFERAY_THEME_PRESET );
        }

        return preset;
    }

    public static IFacetedProjectTemplate getLiferayTemplateForProject( IFacetedProjectWorkingCopy fpjwc )
    {
        IFacetedProjectTemplate template = null;
        String projName = fpjwc.getProjectName();
        IPath location = fpjwc.getProjectLocation();
        String directoryName = StringPool.EMPTY;

        if( location != null )
        {
            directoryName = location.lastSegment();
        }

        if( projName.endsWith( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) )
        {
            template = ProjectFacetsManager.getTemplate( IPluginFacetConstants.LIFERAY_PORTLET_FACET_TEMPLATE_ID );
        }
        else if( projName.endsWith( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) )
        {
            template = ProjectFacetsManager.getTemplate( IPluginFacetConstants.LIFERAY_HOOK_FACET_TEMPLATE_ID );
        }
        else if( projName.endsWith( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) )
        {
            template = ProjectFacetsManager.getTemplate( IPluginFacetConstants.LIFERAY_EXT_FACET_TEMPLATE_ID );
        }
        else if( projName.endsWith( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) )
        {
            template = ProjectFacetsManager.getTemplate( IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_TEMPLATE_ID );
        }
        else if( projName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) ||
            directoryName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) )
        {
            template = ProjectFacetsManager.getTemplate( IPluginFacetConstants.LIFERAY_THEME_FACET_TEMPLATE_ID );
        }

        return template;
    }

    public static String getSDKName( String sdkLocation )
    {
        IPath sdkLocationPath = new Path( sdkLocation );

        SDK sdk = SDKManager.getInstance().getSDK( sdkLocationPath );

        String sdkName = null;

        if( sdk != null )
        {
            sdkName = sdk.getName();
        }
        else
        {
            sdk = SDKUtil.createSDKFromLocation( sdkLocationPath );
            SDKManager.getInstance().addSDK( sdk );
            sdkName = sdk.getName();
        }

        return sdkName;
    }

    private static void addDefaultWebXml( IFacetedProjectWorkingCopy fpjwc, IDataModel dm ) throws CoreException
    {
        // check for existing web.xml file, if not there, add a default one
        // IDE-110 IDE-648
        IPath webinfPath = fpjwc.getProjectLocation().append( "docroot/WEB-INF" ); //$NON-NLS-1$

        if( ProjectUtil.isExtProject( fpjwc.getProject() ) ||
            fpjwc.getProjectLocation().lastSegment().endsWith( "-ext" ) ) //$NON-NLS-1$
        {
            fpjwc.getProjectLocation().append( IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER );
        }

        if( webinfPath.toFile().exists() )
        {
            File webXml = webinfPath.append( "web.xml" ).toFile(); //$NON-NLS-1$

            if( !webXml.exists() )
            {
                ProjectUtil.setGenerateDD( dm, false );

                ProjectUtil.createDefaultWebXml( webXml );

                IProject project = fpjwc.getProject();

                if( project != null )
                {
                    try
                    {
                        project.refreshLocal( IResource.DEPTH_INFINITE, null );
                    }
                    catch( Exception e )
                    {
                        LiferayProjectCore.logError( e );
                    }
                }
            }
        }
    }

    private static class Msgs extends NLS
    {
        public static String noFacetPreset;

        static
        {
            initializeMessages( SDKPluginFacetUtil.class.getName(), Msgs.class );
        }
    }
}
