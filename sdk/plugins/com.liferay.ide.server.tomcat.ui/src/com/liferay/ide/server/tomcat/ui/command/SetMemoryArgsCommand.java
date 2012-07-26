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

package com.liferay.ide.server.tomcat.ui.command;

import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;

import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

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
     *            a Tomcat server
     * @param memoryArgs
     */
    public SetMemoryArgsCommand( LiferayTomcatServer server, String memoryArgs )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.memoryArgs = memoryArgs;
    }

    /**
     * Execute setting the memory args
     */
    public void execute()
    {
        oldMemoryArgs = ( (LiferayTomcatServer) server ).getMemoryArgs();
        ( (LiferayTomcatServer) server ).setMemoryArgs( memoryArgs );
    }

    /**
     * Restore prior memoryargs
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setMemoryArgs( oldMemoryArgs );
    }
}
