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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.BinaryProjectRecord;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.sdk.ISDKConstants;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.SDKManager;
import com.liferay.ide.sdk.util.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@SuppressWarnings( "restriction" )
public class ProjectImportUtil
{

    /**
     * This method was added as part of the IDE-381 fix, this method will collect all the binaries based on the binaries
     * list
     * 
     * @return true if the directory has some binaries
     */
    public static boolean collectBinariesFromDirectory(
        Collection<File> binaryProjectFiles, File directory, boolean recurse, IProgressMonitor monitor )
    {
        if( monitor.isCanceled() )
        {
            return false;
        }

        monitor.subTask( NLS.bind( DataTransferMessages.WizardProjectsImportPage_CheckingMessage, directory.getPath() ) );

        List<String> wildCards = Arrays.asList( ISDKConstants.BINARY_PLUGIN_PROJECT_WILDCARDS );

        WildcardFileFilter wildcardFileFilter = new WildcardFileFilter( wildCards );

        Collection<File> contents = FileUtils.listFiles( directory, wildcardFileFilter, DirectoryFileFilter.INSTANCE );

        if( contents == null )
        {
            return false;
        }
        else
        {
            for( File file : contents )
            {
                if( !binaryProjectFiles.contains( file ) && isValidLiferayPlugin( file ) )
                {
                    binaryProjectFiles.add( file );
                }
            }
        }

        return true;
    }

    /**
     * @param dataModel
     * @param pluginBinaryRecord
     * @param liferaySDK
     * @return
     * @throws IOException
     */
    public static ProjectRecord createPluginProject(
        BridgedRuntime bridgedRuntime, BinaryProjectRecord pluginBinaryRecord, SDK liferaySDK ) throws IOException
    {
        ProjectRecord projectRecord = null;

        if( !pluginBinaryRecord.isConflicts() )
        {
            String displayName = pluginBinaryRecord.getDisplayName();
            String liferayPluginName = pluginBinaryRecord.getLiferayPluginName();
            File binaryFile = pluginBinaryRecord.getBinaryFile();
            IPath projectPath = null;
            IPath sdkPluginProjectFolder = liferaySDK.getLocation();

            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( bridgedRuntime );
            Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( liferayRuntime );
            String docrootFolder = null;

            // Create Project
            if( pluginBinaryRecord.isHook() )
            {
                projectPath = liferaySDK.createNewHookProject( displayName, displayName );

                sdkPluginProjectFolder = sdkPluginProjectFolder.append( ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER );
                docrootFolder = IPluginFacetConstants.HOOK_PLUGIN_SDK_CONFIG_FOLDER;
            }
            else if( pluginBinaryRecord.isPortlet() )
            {
                IPortletFrameworkWizardProvider[] portletFrameworks = ProjectCorePlugin.getPortletFrameworks();
                String portletFrameworkName = null;

                for( int i = 0; i < portletFrameworks.length; i++ )
                {
                    IPortletFrameworkWizardProvider portletFramework = portletFrameworks[i];

                    if( portletFramework.isDefault() )
                    {
                        portletFrameworkName = portletFramework.getShortName();
                        break;
                    }
                }
                projectPath =
                    liferaySDK.createNewPortletProject(
                        displayName, displayName, portletFrameworkName, appServerProperties );
                sdkPluginProjectFolder = sdkPluginProjectFolder.append( ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER );
                docrootFolder = IPluginFacetConstants.PORTLET_PLUGIN_SDK_CONFIG_FOLDER;
            }
            else if( pluginBinaryRecord.isTheme() )
            {
                projectPath = liferaySDK.createNewThemeProject( displayName, displayName );
                sdkPluginProjectFolder = sdkPluginProjectFolder.append( ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER );
                docrootFolder = IPluginFacetConstants.THEME_PLUGIN_SDK_CONFIG_FOLDER;
            }
            else if( pluginBinaryRecord.isLayoutTpl() )
            {
                projectPath = liferaySDK.createNewLayoutTplProject( displayName, displayName, appServerProperties );
                sdkPluginProjectFolder = sdkPluginProjectFolder.append( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER );
                docrootFolder = IPluginFacetConstants.LAYOUTTPL_PLUGIN_SDK_CONFIG_FOLDER;
            }
            else if( pluginBinaryRecord.isExt() )
            {
                projectPath = liferaySDK.createNewExtProject( displayName, displayName, appServerProperties );
                sdkPluginProjectFolder = sdkPluginProjectFolder.append( ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER );
                docrootFolder = IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER;
            }

            // Move the porject to Liferay SDK location
            File tempProjectDir = projectPath.append( liferayPluginName ).toFile();
            // System.out.println( "Source Dir:" + tempProjectDir.getAbsolutePath() );
            File liferayPluginDir = sdkPluginProjectFolder.toFile();
            // System.out.println( "Dest Dir:" + liferayPluginDir.getAbsolutePath() );
            File liferayPluginProjectDir = new File( liferayPluginDir, liferayPluginName );
            FileUtils.copyDirectory( tempProjectDir, liferayPluginProjectDir );

            // Extract the contents
            File docRoot = new File( liferayPluginProjectDir, docrootFolder );
            ZipUtil.unzip( binaryFile, docRoot );

            // IDE-569 check to see if the project already has .project
            File projectFile = new File( liferayPluginProjectDir, ".project" );

            if( projectFile.exists() )
            {
                projectRecord = new ProjectRecord( projectFile );
            }
            else
            {
                projectRecord = new ProjectRecord( liferayPluginProjectDir );
            }

        }

        return projectRecord;

    }

    /**
     * This will create the Eclipse Workspace projects
     * 
     * @param monitor
     * @throws CoreException
     */
    public static void createWorkspaceProjects(
        final Object[] projects, final IRuntime runtime, final String sdkLocation, IProgressMonitor monitor )
        throws CoreException
    {
        final List<IProject> createdProjects = new ArrayList<IProject>();

        monitor.beginTask( "Creating SDK Workspace Projects", projects.length );

        if( projects != null && projects.length > 0 )
        {
            SDK sdk = SDKManager.getInstance().getSDK( new Path( sdkLocation ) );

            // need to add the SDK to workspace if not already available.
            if( sdk == null )
            {
                sdk = SDKUtil.createSDKFromLocation( new Path( sdkLocation ) );
            }

            if( sdk != null && sdk.isValid() && !( SDKManager.getInstance().containsSDK( sdk ) ) )
            {
                SDKManager.getInstance().addSDK( sdk );
            }
        }

        for( int i = 0; i < projects.length; i++ )
        {
            if( projects[i] instanceof ProjectRecord )
            {
                IProject project =
                    ProjectUtil.importProject( (ProjectRecord) projects[i], runtime, sdkLocation, monitor );

                if( project != null )
                {
                    createdProjects.add( project );
                    monitor.worked( createdProjects.size() );
                }
            }
        }
    }

    /**
     * @return
     */
    public static final String getConfigFileLocation( String configFile )
    {
        StringBuilder sb = new StringBuilder( "WEB-INF/" );
        sb.append( configFile );
        return sb.toString();

    }

    /**
     * This method is used to validate whether the given plugin binary is a valid Liferay Plugin Archieve
     * 
     * @param binaryFile
     *            - the binary file to be validated
     * @return
     */
    public static boolean isValidLiferayPlugin( File binaryFile )
    {
        boolean isValid = false;
        try
        {
            JarFile pluginBinary = new JarFile( binaryFile );

            BinaryProjectRecord tempRecord = new BinaryProjectRecord( binaryFile );

            // Check for liferay-plugin-package.properties or liferay-plugin-package.xml
            JarEntry lfrPluginPkgPropsEntry =
                pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ) );
            JarEntry lfrPluginPkgXmlEntry =
                pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_XML_FILE ) );

            if( lfrPluginPkgPropsEntry != null || lfrPluginPkgXmlEntry != null )
            {
                isValid = true;
            }

            if( tempRecord.isHook() )
            {
                isValid =
                    ( isValid && pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.LIFERAY_HOOK_XML_FILE ) ) != null );
            }
            else if( tempRecord.isLayoutTpl() )
            {
                isValid =
                    ( isValid || pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE ) ) != null );
            }
            else if( tempRecord.isPortlet() )
            {
                isValid =
                    ( isValid && pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.LIFERAY_PORTLET_XML_FILE ) ) != null );
            }
            else if( tempRecord.isTheme() )
            {
                isValid =
                    ( isValid || pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE ) ) != null );
            }

            if( !isValid )
            {
                return isValid;
            }
            else
            {
                // check if its a valid web Archieve
                isValid =
                    isValid ||
                        pluginBinary.getJarEntry( getConfigFileLocation( ILiferayConstants.WEB_XML_FILE ) ) != null;
            }
        }
        catch( IOException e )
        {
            isValid = false;
        }

        return isValid;
    }

}
