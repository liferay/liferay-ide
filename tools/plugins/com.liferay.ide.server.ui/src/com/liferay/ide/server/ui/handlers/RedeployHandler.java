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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

import java.util.ArrayList;
import java.util.List;

import com.liferay.ide.server.core.ILiferayServerBehavior;

/**
 * @author Eric Min
 */
@SuppressWarnings( { "restriction", "rawtypes" } )
public class RedeployHandler extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final List<ModuleServer> modules = new ArrayList<ModuleServer>();
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( !selection.isEmpty() )
        {
            if( selection instanceof IStructuredSelection )
            {
                List selectedObj = ( (IStructuredSelection) selection ).toList();

                for( Object object : selectedObj )
                {
                    if( object instanceof ModuleServer )
                    {
                        ModuleServer moduleServer = (ModuleServer) object;
                        modules.add( moduleServer );
                    }
                }
            }
        }

        for( ModuleServer moduleServer : modules )
        {
            final ILiferayServerBehavior liferayServerBehavior =
                (ILiferayServerBehavior) moduleServer.getServer().loadAdapter( ILiferayServerBehavior.class, null );

            if( liferayServerBehavior != null )
            {
                liferayServerBehavior.redeployModule( moduleServer.getModule() );
            }
        }

        return null;
    }
}