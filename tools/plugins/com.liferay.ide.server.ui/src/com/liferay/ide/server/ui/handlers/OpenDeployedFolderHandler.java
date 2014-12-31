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

package com.liferay.ide.server.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

import java.io.IOException;

import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.util.ServerUIUtil;

/**
 * @author Eric Min
 */
@SuppressWarnings( "restriction" )
public class OpenDeployedFolderHandler extends AbstractHandler
{
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            final Object selected = structuredSelection.getFirstElement();

            if( selected != null )
            {
                final IPath folder = getDeployFolderPath( selected );

                if (folder != null)
                {
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
            }
        }

        return null;
    }

    private IPath getDeployFolderPath( Object selected )
    {
        IPath retval = null;

        ModuleServer moduleServer = null;

        if( selected != null )
        {
            if( selected instanceof ModuleServer )
            {
                moduleServer = (ModuleServer) selected;
                moduleServer.getModule()[0].getProject();

                final ILiferayServerBehavior liferayServerBehavior =
                    (ILiferayServerBehavior) moduleServer.getServer().loadAdapter( ILiferayServerBehavior.class, null );

                if( liferayServerBehavior != null )
                {
                    retval = liferayServerBehavior.getDeployedPath( moduleServer.getModule() );
                }
            }
        }

        return retval;
    }
}