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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.ui.LiferayServerUI;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class OpenDeployedFolderAction extends AbstractServerRunningAction
{
    private static final String VARIABLE_FOLDER = "${selected_resource_parent_loc}";
    private static final String VARIABLE_RESOURCE = "${selected_resource_loc}";
    private static final String VARIABLE_RESOURCE_URI = "${selected_resource_uri}";

    public OpenDeployedFolderAction()
    {
        super();
    }

    private String formShowInSytemExplorerCommand( File path ) throws IOException
    {
        String retval = null;

        String command =
            IDEWorkbenchPlugin.getDefault().getPreferenceStore().getString("SYSTEM_EXPLORER");

        if( ! CoreUtil.isNullOrEmpty( command ) )
        {
            command = Util.replaceAll( command, VARIABLE_RESOURCE, quotePath( path.getCanonicalPath() ) );
            command = Util.replaceAll( command, VARIABLE_RESOURCE_URI, path.getCanonicalFile().toURI().toString() );

            final File parent = path.getParentFile();

            if( parent != null )
            {
                retval = Util.replaceAll( command, VARIABLE_FOLDER, quotePath( parent.getCanonicalPath() ) );
            }
        }

        return retval;
    }

    private IPath getDeployFolderPath()
    {
        IPath retval = null;

        if( selectedModule != null )
        {
            selectedModule.getModule()[0].getProject();

            final ILiferayServerBehavior liferayServerBehavior =
                (ILiferayServerBehavior) selectedModule.getServer().loadAdapter( ILiferayServerBehavior.class, null );

            if( liferayServerBehavior != null )
            {
                retval = liferayServerBehavior.getDeployedPath( selectedModule.getModule() );
            }
        }

        return retval;
    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED | IServer.STATE_STOPPED | IServer.STATE_STARTING | IServer.STATE_STOPPING |
            IServer.STATE_UNKNOWN;
    }

    private String quotePath( String path )
    {
        if( Util.isLinux() || Util.isMac() )
        {
            // Quote for usage inside "", man sh, topic QUOTING:
            path = path.replaceAll( "[\"$`]", "\\\\$0" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Windows: Can't quote, since explorer.exe has a very special command line parsing strategy.
        return path;
    }

    public void run( IAction action )
    {
        if( selectedModule == null )
        {
            return; // can't do anything if server has not been selected
        }

        final IPath folder = getDeployFolderPath();

        try
        {
            String launchCmd = formShowInSytemExplorerCommand( folder.toFile() );

            if( Util.isLinux() || Util.isMac() )
            {
                Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", launchCmd }, null, folder.toFile() );
            }
            else
            {
                Runtime.getRuntime().exec( launchCmd, null, folder.toFile() );
            }
        }
        catch( IOException e )
        {
            LiferayServerUI.logError( "Unable to execute command", e );
        }
    }

    @Override
    public void selectionChanged( IAction action, ISelection selection )
    {
        super.selectionChanged( action, selection );

        if( action.isEnabled() )
        {
            final IPath deployedPath = getDeployFolderPath();

            try
            {
                if( deployedPath == null || ( !deployedPath.toFile().exists() ) ||
                    CoreUtil.isNullOrEmpty( formShowInSytemExplorerCommand( deployedPath.toFile() ) ) )
                {
                    action.setEnabled( false );
                }
            }
            catch( IOException e )
            {
                action.setEnabled( false );
            }
        }
    }
}
