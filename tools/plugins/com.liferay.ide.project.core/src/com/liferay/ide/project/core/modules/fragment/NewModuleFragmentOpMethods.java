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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class NewModuleFragmentOpMethods
{

    public static final Status execute( final NewModuleFragmentOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay module fragment project (this process may take several minutes)", 100 );

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewModuleFragmentOp> projectProvider =
                op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );

            if( retval.ok() )
            {
                updateBuildPrefs( op );
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module fragment project.";
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static void copyOverrideFiles( NewModuleFragmentOp op )
    {
        final String hostBundleName = op.getHostOsgiBundle().content();

        final IPath temp = ProjectCore.getDefault().getStateLocation().append(
            hostBundleName.substring( 0, hostBundleName.lastIndexOf( ".jar" ) ) );

        final String projectName = op.getProjectName().content();

        final IPath location = PathBridge.create( op.getLocation().content() );

        final ElementList<OverrideFilePath> files = op.getOverrideFiles();

        for( OverrideFilePath file : files )
        {
            File fragmentFile = temp.append( file.getValue().content() ).toFile();

            if( fragmentFile.exists() )
            {
                File folder = null;

                if( fragmentFile.getName().equals( "portlet.properties" ) )
                {
                    folder = location.append( projectName ).append( "src/main/java" ).toFile();

                    FileUtil.copyFileToDir( fragmentFile, "portlet-ext.properties", folder );
                }
                else
                {
                    String parent = fragmentFile.getParentFile().getPath();
                    parent = parent.replaceAll( "\\\\", "/" );
                    String metaInfResources = "META-INF/resources";

                    parent = parent.substring( parent.indexOf( metaInfResources ) + metaInfResources.length() );

                    IPath resources = location.append( projectName ).append( "src/main/resources/META-INF/resources" );

                    folder = resources.toFile();
                    folder.mkdirs();

                    if( !parent.equals( "resources" ) && !parent.equals( "" ) )
                    {
                        folder = resources.append( parent ).toFile();
                        folder.mkdirs();
                    }

                    FileUtil.copyFileToDir( fragmentFile, folder );
                }
            }
        }
    }

    public static String[] getBsnAndVersion( NewModuleFragmentOp op ) throws CoreException
    {
        final String hostBundleName = op.getHostOsgiBundle().content();

        final IPath temp = ProjectCore.getDefault().getStateLocation().append(
            hostBundleName.substring( 0, hostBundleName.lastIndexOf( ".jar" ) ) );

        if( !temp.toFile().exists() )
        {
            final IRuntime runtime = ServerUtil.getRuntime( op.getLiferayRuntimeName().content() );

            final PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

            File hostBundle = portalBundle.getOSGiBundlesDir().append( "modules" ).append( hostBundleName ).toFile();

            if( !hostBundle.exists() )
            {
                hostBundle = ProjectCore.getDefault().getStateLocation().append( hostBundleName ).toFile();
            }

            try
            {
                ZipUtil.unzip( hostBundle, temp.toFile() );
            }
            catch( IOException e )
            {
                throw new CoreException( ProjectCore.createErrorStatus( e ) );
            }
        }

        String bundleSymbolicName = "";
        String version = "";

        if( temp.toFile().exists() )
        {
            final File file = temp.append( "META-INF/MANIFEST.MF" ).toFile();
            final String[] contents = FileUtil.readLinesFromFile( file );

            for( String content : contents )
            {
                if( content.contains( "Bundle-SymbolicName:" ) )
                {
                    bundleSymbolicName = content.substring(
                        content.indexOf( "Bundle-SymbolicName:" ) + "Bundle-SymbolicName:".length() );
                }

                if( content.contains( "Bundle-Version:" ) )
                {
                    version =
                        content.substring( content.indexOf( "Bundle-Version:" ) + "Bundle-Version:".length() ).trim();
                }
            }
        }

        return new String[] { bundleSymbolicName, version };
    }

    public static String getMavenParentPomGroupId( NewModuleFragmentOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( groupId ) )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewModuleFragmentOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( version ) )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    private static void updateBuildPrefs( final NewModuleFragmentOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

            prefs.put( ProjectCore.PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION,
                op.getProjectProvider().text() );

            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error updating default project build type.";
            ProjectCore.logError( msg, e );
        }
    }
}
