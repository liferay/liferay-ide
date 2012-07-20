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
 * Command to change the external properties
 */
@SuppressWarnings( "restriction" )
public class SetExternalPropertiesCommand extends ServerCommand
{

    protected String externalProperties;
    protected String oldExternalProperties;

    /**
     * Constructs command to set the deploy directory.
     * 
     * @param server
     *            a Tomcat server
     * @param deployDir
     *            deployment directory to set
     */
    public SetExternalPropertiesCommand( LiferayTomcatServer server, String externalProperties )
    {
        super( server, "Set External properties" );
        this.externalProperties = externalProperties;
    }

    /**
     * Execute setting the external properties
     */
    public void execute()
    {
        oldExternalProperties = ( (LiferayTomcatServer) server ).getExternalProperties();
        ( (LiferayTomcatServer) server ).setExternalProperties( externalProperties );
    }

    /**
     * Restore prior external properties
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setExternalProperties( oldExternalProperties );
    }
}
