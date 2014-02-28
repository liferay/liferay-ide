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
 * Command to change the server model
 */
@SuppressWarnings( "restriction" )
public class SetServerModeCommand extends ServerCommand
{

    protected int serverMode;
    protected int oldServerMode;

    /**
     * Constructs command to set the server mode
     *
     * @param server
     *            a Tomcat server
     * @param server
     *            mode
     */
    public SetServerModeCommand( LiferayTomcatServer server, int serverMode )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.serverMode = serverMode;
    }

    /**
     * Execute setting the server model
     */
    public void execute()
    {
        oldServerMode = ( (LiferayTomcatServer) server ).getServerMode();
        ( (LiferayTomcatServer) server ).setServerMode( serverMode );
    }

    /**
     * Restore prior server model
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setServerMode( oldServerMode );
    }

}
