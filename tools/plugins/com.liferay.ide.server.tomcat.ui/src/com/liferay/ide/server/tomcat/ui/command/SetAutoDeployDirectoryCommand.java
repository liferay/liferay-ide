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
 * Command to change the deploy directory
 */
@SuppressWarnings( "restriction" )
public class SetAutoDeployDirectoryCommand extends ServerCommand
{

    protected String autoDeployDir;
    protected String oldAutoDeployDir;

    /**
     * Constructs command to set the deploy directory.
     * 
     * @param server
     *            a Tomcat server
     * @param deployDir
     *            deployment directory to set
     */
    public SetAutoDeployDirectoryCommand( LiferayTomcatServer server, String autoDeployDir )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.autoDeployDir = autoDeployDir;
    }

    /**
     * Execute setting the deploy directory
     */
    public void execute()
    {
        oldAutoDeployDir = ( (LiferayTomcatServer) server ).getAutoDeployDirectory();
        ( (LiferayTomcatServer) server ).setAutoDeployDirectory( autoDeployDir );
    }

    /**
     * Restore prior deploy directory
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setAutoDeployDirectory( oldAutoDeployDir );
    }
}
