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
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.server.ui.util.ServerUIUtil;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Terry Jia
 */
public class OpenLiferayHomeFolderServerAction extends AbstractServerRunningAction
{

    public OpenLiferayHomeFolderServerAction()
    {
        super();
    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED | IServer.STATE_STARTING | IServer.STATE_STOPPING | IServer.STATE_STOPPED;
    }

    public void run( IAction action )
    {
        if( selectedServer != null )
        {

            final IPath path = selectedServer.getRuntime().getLocation();

            if( path != null )
            {
                try
                {
                    final String launchCmd = ServerUIUtil.getSystemExplorerCommand( path.toFile() );

                    if( !CoreUtil.isNullOrEmpty( launchCmd ) )
                    {
                        ServerUIUtil.openInSystemExplorer( launchCmd, path.removeLastSegments( 1 ).toFile() );
                    }
                }
                catch( IOException e )
                {
                    LiferayServerUIPlugin.logError( "Error opening portal home folder.", e );
                }
            }
        }
    }

    @Override
    public void selectionChanged( IAction action, ISelection selection )
    {
        super.selectionChanged( action, selection );

        if( ( selectedServer != null ) && ( selection instanceof IStructuredSelection ) )
        {
            final IPath path = selectedServer.getRuntime().getLocation();

            boolean enabled = false;

            if( path != null )
            {
                try
                {
                    final IStructuredSelection sel = (IStructuredSelection) selection;

                    final String launchCmd = ServerUIUtil.getSystemExplorerCommand( path.toFile() );

                    if( !CoreUtil.isNullOrEmpty( launchCmd ) && ( sel.toList().size() == 1 ) )
                    {
                        enabled = true;
                    }
                }
                catch( IOException e )
                {
                }
            }

            action.setEnabled( enabled );
        }
    }

}
