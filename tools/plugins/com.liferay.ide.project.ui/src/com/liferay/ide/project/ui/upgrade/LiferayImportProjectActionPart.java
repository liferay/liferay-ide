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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.IOUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Simon Jiang
 */
public class LiferayImportProjectActionPart extends  AbstractLiferayUpgradeNavigatorPart
{
    private final String buttonLabel = "&Import Project";
    private FilteredListener<PropertyContentEvent> listener;
    
    protected Button button;

    protected CodeUpgradeOp op()
    {
        return getLocalModelElement().nearest( CodeUpgradeOp.class );
    }
    
    @Override
    protected void createButton( Composite composite, Presentation parent )
    {

        button = createButtonField( composite );
        button.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                importProject( parent );
            }
            
        } );
        button.setText( buttonLabel );

    }
    
    @Override
    protected void init()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                if( event.property().definition().equals( CodeUpgradeOp.PROP_CONFIRM ) )
                {
                    Boolean confirm = op().getConfirm().content();

                    if ( confirm != null && confirm.booleanValue() == true )
                    {
                        button.setEnabled( true );
                    }
                    else
                    {
                        button.setEnabled( false );
                    }
                }
            }
        };

        op().property( CodeUpgradeOp.PROP_CONFIRM ).attach( this.listener );
    }
    
    
    private static String newPath = "";

    
    protected void importProject( Presentation context )
    {
        String layout = op( context ).getLayout().content();

        IPath location = PathBridge.create( op( context ).getSdkLocation().content() );
        String projectName = op( context ).getProjectName().content();

        try
        {
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile( new IRunnableWithProgress()
            {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    try
                    {
                        copyNewSDK( location, monitor );

                        clearWorkspaceSDKAndProjects( location, monitor );

                        if( layout.equals( "Use plugin sdk in liferay workspace" ) )
                        {
                            createLiferayWorkspace( location, monitor );

                            newPath = renameProjectFolder( location, projectName, monitor );

                            ILiferayProjectImporter importer = LiferayCore.getImporter( "gradle" );

                            importer.importProject( newPath, monitor );

                            importSDKProject( new Path( newPath ).append( "plugins-sdk" ), monitor );
                        }
                        else
                        {
                            String serverName = op( context ).getLiferayServerName().content();

                            IServer server = ServerUtil.getServer( serverName );

                            IPath serverPath = server.getRuntime().getLocation();

                            SDK sdk = new SDK( location );
                            sdk.addOrUpdateServerProperties( serverPath );

                            newPath = renameProjectFolder( location, projectName, monitor );

                            sdk = SDKUtil.createSDKFromLocation( new Path( newPath ) );

                            SDKUtil.openAsProject( sdk, monitor );

                            importSDKProject( sdk.getLocation(), monitor );
                        }

                    }
                    catch( Exception e )
                    {
                    }
                }
            } );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        op( context ).setNewLocation( newPath );

        newPath = "";

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
        final URL sdkZipUrl = Platform.getBundle( "com.liferay.ide.project.ui" ).getEntry( "resources/sdk70ga2.zip" );

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

            for( File project : eclipseProjectFiles )
            {
                try
                {
                    ProjectImportUtil.importProject( new Path( project.getParent() ), monitor, null );
                }
                catch( CoreException e )
                {
                }
            }
        }
    }
    
}
