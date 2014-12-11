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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.server.ui.util.ServerUIUtil;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "rawtypes" )
public class OpenLiferayHomeFolderHandler extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            List selectedObj = ( (IStructuredSelection) selection ).toList();

            if( ( selectedObj.size() == 1 ) && ( selectedObj.get( 0 ) instanceof IServer ) )
            {
                IServer server = (IServer) selectedObj.get( 0 );

                final IPath path = server.getRuntime().getLocation();

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

        return null;
    }

}
