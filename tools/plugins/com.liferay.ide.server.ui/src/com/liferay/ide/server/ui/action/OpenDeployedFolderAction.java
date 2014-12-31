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

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.server.core.IServer;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.util.ServerUIUtil;

/**
 * @author Gregory Amerson
 */
public class OpenDeployedFolderAction extends AbstractServerRunningAction
{
    public OpenDeployedFolderAction()
    {
        super();
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

    public void run( IAction action )
    {
        if( selectedModule == null )
        {
            return; // can't do anything if server has not been selected
        }

        final IPath folder = getDeployFolderPath();

        try
        {
            String launchCmd = ServerUIUtil.getSystemExplorerCommand( folder.toFile() );

            ServerUIUtil.openInSystemExplorer( launchCmd, folder.toFile() );
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
                    CoreUtil.isNullOrEmpty( ServerUIUtil.getSystemExplorerCommand( deployedPath.toFile() ) ) )
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
