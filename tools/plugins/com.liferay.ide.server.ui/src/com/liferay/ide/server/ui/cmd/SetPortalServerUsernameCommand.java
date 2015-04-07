/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

@SuppressWarnings( "restriction" )
public class SetPortalServerUsernameCommand extends ServerCommand
{

    protected String oldUsername;
    protected String username;

    public SetPortalServerUsernameCommand( IServerWorkingCopy server, String username )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.username = username;
    }

    /**
     * Execute setting the memory args
     */
    public void execute()
    {
        oldUsername = ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getUsername();
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setUsername( username );
    }

    /**
     * Restore prior memoryargs
     */
    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setUsername( oldUsername );
    }
}
