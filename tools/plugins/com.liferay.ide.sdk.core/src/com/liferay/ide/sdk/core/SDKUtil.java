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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Greg Amerson
 * @author Lovett Li
 * @author Simon Jiang
 */
public class SDKUtil
{

    public static int countPossibleWorkspaceSDKProjects()
    {
        int sdkCount = 0;
        final IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            if( isValidSDKLocation( project.getLocation().toOSString() ) )
            {
                sdkCount++;
            }
        }

        return sdkCount;
    }

    public static SDK createSDKFromLocation( IPath path )
    {
        try
        {
            if( isValidSDKLocation( path.toOSString() ) )
            {
                SDK sdk = new SDK( path );

                sdk.setName( path.lastSegment() );

                return sdk;
            }
        }
        catch( Exception e )
        {
            // ignore errors
        }

        return null;
    }

    public static SDK getSDK( IProject project )
    {
        SDK retval = null;

        // try to determine SDK based on project location
        IPath projectLocation = project.getRawLocation();

        if( projectLocation == null )
        {
            projectLocation = project.getLocation();
        }

        if( projectLocation != null )
        {
            IPath sdkLocation = projectLocation.removeLastSegments( 2 );

            retval = SDKManager.getInstance().getSDK( sdkLocation );

            if( retval == null )
            {
                retval = SDKUtil.createSDKFromLocation( sdkLocation );

                if( retval != null )
                {
                    final SDK newSDK = retval;

                    SDKManager.getInstance().addSDK( newSDK );
                }
            }
        }

        if( retval == null )
        {
            // this means the sdk could not be determined by location (user is using out-of-sdk style projects)
            // so we should check to see if the sdk name is persisted to the project prefs
            final IScopeContext[] context = new IScopeContext[] { new ProjectScope( project ) };
            final String sdkName =
                Platform.getPreferencesService().getString(
                    SDKCorePlugin.PLUGIN_ID, SDKCorePlugin.PREF_KEY_SDK_NAME, null, context );

            retval = SDKManager.getInstance().getSDK( sdkName );
        }


        return retval;
    }

    public static SDK getSDKFromProjectDir( File projectDir )
    {
        File sdkDir = projectDir.getParentFile().getParentFile();

        if( sdkDir.exists() && SDKUtil.isValidSDKLocation( sdkDir.getPath() ) )
        {
            Path sdkLocation = new Path( sdkDir.getPath() );

            SDK existingSDK = SDKManager.getInstance().getSDK( sdkLocation );

            if( existingSDK != null )
            {
                return existingSDK;
            }
            else
            {
                return createSDKFromLocation( sdkLocation );
            }
        }

        return null;
    }

    public static IProject getWorkspaceSDKProject() throws CoreException
    {
        IProject retval = null;
        final IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            if( isValidSDKLocation( project.getLocation().toOSString() ) )
            {
                if( retval != null )
                {
                    throw new CoreException( SDKCorePlugin.createErrorStatus(
                        new IllegalStateException( "Workspace can't have more than one SDK project open" ) ) );
                }

                retval = project;
            }
        }

        return retval;
    }

    public static boolean hasGradleTools( IPath path )
    {
        return path.append( "tools" ).append( "gradle" ).toFile().exists();
    }

    public static boolean isIvyProject( IProject project )
    {
        try
        {
            return isSDKProject( project ) && project.hasNature( "org.apache.ivyde.eclipse.ivynature" ); //$NON-NLS-1$
        }
        catch( CoreException e )
        {
        }

        return false;
    }

    public static boolean isSDKProject( IProject project )
    {
        if( project == null || ( !project.exists() ) || ( !project.isAccessible() ) )
        {
            return false;
        }

        return getSDK( project ) != null;
    }

    public static boolean isSDKSupported( String location )
    {
        boolean retval = false;

        try
        {
            String version = SDKUtil.readSDKVersion( location );

            retval = CoreUtil.compareVersions( new Version( version ), ISDKConstants.LEAST_SUPPORTED_SDK_VERSION ) >= 0;
        }
        catch( Exception e )
        {
            // best effort we didn't find a valid location
        }

        return retval;
    }

    public static boolean isValidSDKLocation( String loc )
    {
        boolean retval = false;

        try
        {
            File sdkDir = new File( loc );

            File buildProperties = new File( sdkDir, ISDKConstants.BUILD_PROPERTIES );
            File portletsBuildXml = new File( sdkDir, ISDKConstants.PORTLET_PLUGIN_ANT_BUILD );
            File hooksBuildXml = new File( sdkDir, ISDKConstants.HOOK_PLUGIN_ANT_BUILD );

            retval =
                buildProperties.exists() && portletsBuildXml.exists() && hooksBuildXml.exists();
        }
        catch( Exception e )
        {
            // best effort we didn't find a valid location
        }

        return retval;
    }

    public static boolean isValidSDKVersion( String sdkVersion, Version lowestValidVersion )
    {
        Version sdkVersionValue = null;

        try
        {
            sdkVersionValue = new Version( sdkVersion );
        }
        catch( Exception ex )
        {
            // ignore means we don't have valid version
        }

        if( sdkVersionValue != null && CoreUtil.compareVersions( sdkVersionValue, lowestValidVersion ) >= 0 )
        {
            return true;
        }

        return false;
    }

    public static void openAsProject( SDK sdk ) throws CoreException
    {
        openAsProject( sdk, new NullProgressMonitor() );
    }

    public static void openAsProject( SDK sdk, IProgressMonitor monitor ) throws CoreException
    {
        final IProject sdkProject = CoreUtil.getProject( sdk.getName() );

        if( sdkProject == null || ( !sdkProject.exists() ) )
        {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();

            final IProjectDescription description = workspace.newProjectDescription( sdk.getLocation().lastSegment() );
            description.setLocationURI( sdk.getLocation().toFile().toURI() );

            sdkProject.create( description, monitor );

            IPath settingFolderPath = sdkProject.getLocation().append( ".settings" );
            File settingFolder = settingFolderPath.toFile();

            if( !settingFolder.exists() )
            {
                settingFolder.mkdir();
            }

            if( !settingFolderPath.append( "org.eclipse.wst.validation.prefs" ).toFile().exists() )
            {
                try
                {
                    URL url =
                        FileLocator.toFileURL( SDKCorePlugin.getDefault().getBundle().getEntry(
                            "files/org.eclipse.wst.validation.prefs" ) );

                    File file = new File( url.getFile() );

                    FileUtil.copyFileToDir( file, settingFolder );
                }
                catch( IOException e )
                {
                }
            }

            sdkProject.open( monitor );
        }
    }

    static String readSDKVersion( String path ) throws FileNotFoundException, IOException
    {
        Properties properties = new Properties();
        properties.load( new FileInputStream( new Path( path ).append( "build.properties" ).toFile() ) ); //$NON-NLS-1$

        return properties.getProperty( "lp.version" ); //$NON-NLS-1$
    }

    public static void saveSDKNameSetting( IProject project, String sdkName)
    {
        try
        {
            final IEclipsePreferences prefs = new ProjectScope( project ).getNode( SDKCorePlugin.PLUGIN_ID );
            prefs.put( SDKCorePlugin.PREF_KEY_SDK_NAME, sdkName );
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            SDKCorePlugin.logError( "Unable to persist sdk name to project " + project, e );  //$NON-NLS-1$
        }
    }

    public static SDK getWorkspaceSDK() throws CoreException
    {
        SDK sdk = null;
        IProject workspaceSDKProject = getWorkspaceSDKProject();

        if (workspaceSDKProject != null)
        {
            sdk = SDKUtil.createSDKFromLocation( workspaceSDKProject.getLocation() );
        }

        return sdk;
    }

    public static IProject[] getWorkspaceSDKs()
    {
        List<IProject> sdkProjects = new ArrayList<IProject>();
        final IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            if( isValidSDKLocation( project.getLocation().toOSString() ) )
            {
                sdkProjects.add( project );
            }
        }

        return sdkProjects.toArray( new IProject[sdkProjects.size()] );
    }
}