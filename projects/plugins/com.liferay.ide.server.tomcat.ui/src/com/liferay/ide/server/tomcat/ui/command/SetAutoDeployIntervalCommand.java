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

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

/**
 * Command to change the deploy directory
 */
@SuppressWarnings( "restriction" )
public class SetAutoDeployIntervalCommand extends ServerCommand
{

    protected String autoDeployInterval;
    protected String oldAutoDeployInterval;

    /**
     * Constructs command to set the deploy directory.
     * 
     * @param server
     *            a Tomcat server
     * @param deployDir
     *            deployment directory to set
     */
    public SetAutoDeployIntervalCommand( LiferayTomcatServer server, String autoDeployInterval )
    {
        super( server, "Set Auto Deploy Interval" );
        this.autoDeployInterval = autoDeployInterval;
    }

    /**
     * Execute setting the deploy directory
     */
    public void execute()
    {
        oldAutoDeployInterval = ( (LiferayTomcatServer) server ).getAutoDeployInterval();
        ( (LiferayTomcatServer) server ).setAutoDeployInterval( autoDeployInterval );
    }

    /**
     * Restore prior deploy directory
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setAutoDeployInterval( oldAutoDeployInterval );
    }
}
