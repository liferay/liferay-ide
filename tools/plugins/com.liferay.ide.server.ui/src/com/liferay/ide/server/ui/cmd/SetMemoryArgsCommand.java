/*******************************************************************************
 * Copyright (c) 2010 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *     Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * Command to change the memory arguments
 */
@SuppressWarnings( "restriction" )
public class SetMemoryArgsCommand extends ServerCommand
{

    protected String memoryArgs;
    protected String oldMemoryArgs;

    /**
     * Constructs command to set the memory arguments
     * 
     * @param server
     * @param memoryArgs
     */
    public SetMemoryArgsCommand( IServerWorkingCopy server, String memoryArgs )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.memoryArgs = memoryArgs;
    }

    /**
     * Execute setting the memory args
     */
    public void execute()
    {
        oldMemoryArgs =
            StringUtil.merge( ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getMemoryArgs(), " " );
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setMemoryArgs( memoryArgs );
    }

    /**
     * Restore prior memoryargs
     */
    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setMemoryArgs( oldMemoryArgs );
    }
}
