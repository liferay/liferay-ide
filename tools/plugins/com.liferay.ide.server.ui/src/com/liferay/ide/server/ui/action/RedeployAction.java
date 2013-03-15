/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.server.core.ILiferayServerBehavior;

import org.eclipse.jface.action.IAction;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
public class RedeployAction extends AbstractServerRunningAction
{

    public RedeployAction()
    {
        super();
    }

    public void run( IAction action )
    {
        if( selectedModule == null )
        {
            return; // can't do anything if server has not been selected
        }

        if( selectedModule != null )
        {
            selectedModule.getModule()[0].getProject();
            ILiferayServerBehavior liferayServerBehavior =
                (ILiferayServerBehavior) selectedModule.getServer().loadAdapter( ILiferayServerBehavior.class, null );

            if( liferayServerBehavior != null )
            {
                liferayServerBehavior.redeployModule( selectedModule.getModule() );
            }
        }

    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED | IServer.STATE_STOPPED;
    }
}
