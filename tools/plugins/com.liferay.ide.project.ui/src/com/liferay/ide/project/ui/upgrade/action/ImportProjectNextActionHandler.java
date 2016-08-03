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

package com.liferay.ide.project.ui.upgrade.action;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.wst.server.core.IServer;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.IOUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Terry Jia
 */
public class ImportProjectNextActionHandler extends BaseActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        String layout = op( context ).getLayout().content();

        IPath location = PathBridge.create( op( context ).getLocation().content() );
        String projectName = op( context ).getProjectName().content();

        NullProgressMonitor npm = new NullProgressMonitor();

        try
        {
            copyNewSDK( location, npm );

            clearWorkspaceSDKAndProjects( location, npm );

            if( layout.equals( "Use plugin sdk in liferay workspace" ) )
            {
                createLiferayWorkspace( location, npm );

                String wsPath = renameProjectFolder( location, projectName, npm );

                op(context).setNewLocation( wsPath );

                ILiferayProjectImporter importer = LiferayCore.getImporter( "gradle" );

                importer.importProject( wsPath, npm );

                importSDKProject( new Path( wsPath ).append( "plugins-sdk" ), npm );
            }
            else
            {
                String serverName = op( context ).getLiferayServerName().content();

                IServer server = ServerUtil.getServer( serverName );

                IPath serverPath = server.getRuntime().getLocation();

                SDK sdk = new SDK( location );
                sdk.addOrUpdateServerProperties( serverPath );

                String newPath = renameProjectFolder( location, projectName, npm );

                op(context).setNewLocation( newPath );

                sdk = SDKUtil.createSDKFromLocation( new Path( newPath ) );

                SDKUtil.openAsProject( sdk, npm );

                importSDKProject( sdk.getLocation(), npm );
            }
        }
        catch( Exception e )
        {
            ProjectUI.createErrorStatus( "Convent failed", e );
        }

        setOutlineSelection( context, STEP_IMPORT_PROJECT );

        return null;
    }

    private void clearWorkspaceSDKAndProjects( IPath targetSDKLocation, IProgressMonitor monitor ) throws CoreException
    {
        IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

        if( sdkProject != null && sdkProject.getLocation().equals( targetSDKLocation ) )
        {
            IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();

            for( IProject project : projects )
            {
                project.delete( false, true, monitor );
            }

            sdkProject.delete( false, true, monitor );
        }
        
    }

    private void copyNewSDK( IPath targetSDKLocation, IProgressMonitor monitor ) throws IOException
    {
        final URL sdkZipUrl = Platform.getBundle( "com.liferay.ide.project.ui" ).getEntry( "sdk/sdk70ga2.zip" );

        final File sdkZipFile = new File( FileLocator.toFileURL( sdkZipUrl ).getFile() );

        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        File stateDir = stateLocation.toFile();

        ZipUtil.unzip( sdkZipFile, stateDir );

        IOUtil.copyDirToDir( new File( stateDir, "com.liferay.portal.plugins.sdk-7.0" ), targetSDKLocation.toFile() );
    }

    private void createLiferayWorkspace( IPath targetSDKLocation, IProgressMonitor monitor ) throws BladeCLIException
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + targetSDKLocation.toFile().getAbsolutePath() + "\" " );
        sb.append( "init" );

        BladeCLI.execute( sb.toString() );
    }

    private String renameProjectFolder( IPath targetSDKLocation, String newName, IProgressMonitor monitor )
    {
        if( newName == null || newName.equals( "" ) )
        {
            return targetSDKLocation.toString();
        }

        File newFolder = targetSDKLocation.removeLastSegments( 1 ).append( newName ).toFile();
        targetSDKLocation.toFile().renameTo( newFolder );
        return newFolder.toPath().toString();
    }

    private void importSDKProject( IPath targetSDKLocation, IProgressMonitor monitor )
    {
        Collection<File> eclipseProjectFiles = new ArrayList<File>();

        Collection<File> liferayProjectDirs = new ArrayList<File>();

        if( ProjectUtil.collectSDKProjectsFromDirectory(
            eclipseProjectFiles, liferayProjectDirs, targetSDKLocation.toFile(), null, true, monitor ) )
        {
            for( File project : liferayProjectDirs )
            {
                try
                {
                    ProjectImportUtil.importProject( new Path( project.getPath() ), monitor, null );
                }
                catch( CoreException e )
                {
                }
            }
        }
    }

}
