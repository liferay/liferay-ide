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
 * Command to change the user timezone
 */
@SuppressWarnings( "restriction" )
public class SetUserTimezoneCommand extends ServerCommand
{

    protected String userTimezone;
    protected String oldUserTimezone;

    /**
     * Constructs command to set the user timezone
     * 
     * @param server
     *            a Tomcat server
     * @param userTimezone
     */
    public SetUserTimezoneCommand( LiferayTomcatServer server, String userTimezone )
    {
        super( server, Messages.serverEditorActionSetDeployDirectory );
        this.userTimezone = userTimezone;
    }

    /**
     * Execute setting the user timezone
     */
    public void execute()
    {
        oldUserTimezone = ( (LiferayTomcatServer) server ).getUserTimezone();
        ( (LiferayTomcatServer) server ).setUserTimezone( userTimezone );
    }

    /**
     * Restore prior userTimezone
     */
    public void undo()
    {
        ( (LiferayTomcatServer) server ).setUserTimezone( oldUserTimezone );
    }
}
