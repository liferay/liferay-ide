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

package com.liferay.ide.ui.workspace;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;


/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class LaunchLiferay7WorkspaceAction extends Action
{

    public LaunchLiferay7WorkspaceAction()
    {
        super( "Liferay 7 Workspace" );
    }


    @Override
    public void run()
    {
        LaunchWorkspaceHandler hanlder = new LaunchWorkspaceHandler();

        Map<String, String> map = new HashMap<String, String>();

        // TODO which workspace shall we point to ?
        map.put( LaunchWorkspaceHandler.PARAM_WORKSPACE_LOCATION, "D:/test/forlaunch" );

        ExecutionEvent event = new ExecutionEvent( null, map, null, null );

        try
        {
            hanlder.execute( event );
        }
        catch( ExecutionException e )
        {
        }
    }
}
