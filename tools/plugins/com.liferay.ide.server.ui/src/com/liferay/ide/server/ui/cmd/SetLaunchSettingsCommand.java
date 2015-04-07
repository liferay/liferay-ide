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

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * Command to change the server model
 *
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class SetLaunchSettingsCommand extends ServerCommand
{

    protected boolean launchSettings;
    protected boolean oldLaunchSettings;

    /**
     * Constructs command to set launch setting
     *
     * @param server
     * @param value
     *            of launchSettings
     */
    public SetLaunchSettingsCommand( IServerWorkingCopy server, boolean launchSettings )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.launchSettings = launchSettings;
    }

    /**
     * Execute setting launch setting propety
     */
    public void execute()
    {
        oldLaunchSettings = ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getLaunchSettings();
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setLaunchSettings( launchSettings );
    }

    /**
     * Restore prior portalServerSettings prooperty
     */
    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setLaunchSettings( oldLaunchSettings );
    }
}