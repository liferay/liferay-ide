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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.IServerModule;

/**
 * @author Greg Amerson
 */
public abstract class AbstractServerRunningAction implements IObjectActionDelegate
{

    protected IServer selectedServer;
    protected IServerModule selectedModule;
    protected IWorkbenchPart activePart;

    public AbstractServerRunningAction()
    {
        super();
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
        selectedServer = null;

        if( !selection.isEmpty() )
        {
            if( selection instanceof IStructuredSelection )
            {
                Object obj = ( (IStructuredSelection) selection ).getFirstElement();

                if( obj instanceof IServer )
                {
                    selectedServer = (IServer) obj;

                    action.setEnabled( ( selectedServer.getServerState() & getRequiredServerState() ) > 0 );
                }
                else if( obj instanceof IServerModule )
                {
                    selectedModule = (IServerModule) obj;

                    action.setEnabled( ( selectedModule.getServer().getServerState() & getRequiredServerState() ) > 0 );
                }
            }
        }
    }

    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
        this.activePart = targetPart;
    }

    protected IWorkbenchPart getActivePart()
    {
        return this.activePart;
    }

    protected Shell getActiveShell()
    {
        if( getActivePart() != null )
        {
            return getActivePart().getSite().getShell();
        }
        else
        {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        }
    }

    protected abstract int getRequiredServerState();

}
