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

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.wst.server.core.IServer;

import com.liferay.ide.ui.LiferayUIPlugin;

/**
 * @author Greg Amerson
 */
public abstract class OpenPortalURLAction extends AbstractServerRunningAction
{

    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            final ICommandService actionService =
                (ICommandService) PlatformUI.getWorkbench().getService( ICommandService.class );

            final Command actionCmd = actionService.getCommand( getCommandId() );

            try
            {
                actionCmd.executeWithChecks( new ExecutionEvent() );
            }
            catch( Exception e )
            {
                LiferayUIPlugin.logError( "Error running command " + getCommandId(), e );
            }
        }
    }

    protected abstract String getCommandId();

    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED | IServer.STATE_STOPPED;
    }
}