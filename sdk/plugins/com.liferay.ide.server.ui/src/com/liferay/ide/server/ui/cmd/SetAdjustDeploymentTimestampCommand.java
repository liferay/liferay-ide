/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.remote.IRemoteServer;

import org.eclipse.wst.server.ui.internal.command.ServerCommand;

@SuppressWarnings( "restriction" )
public class SetAdjustDeploymentTimestampCommand extends ServerCommand
{

    protected boolean adjustDemploymentTimestamp;
    protected boolean oldAdjustDemploymentTimestamp;
    protected IRemoteServer remoteServer;

    public SetAdjustDeploymentTimestampCommand( IRemoteServer server, boolean adjustTimestamp )
    {
        super( null, "Set Deploy Custom Portlet XML" );
        this.remoteServer = server;
        this.adjustDemploymentTimestamp = adjustTimestamp;
    }

    public void execute()
    {
        oldAdjustDemploymentTimestamp = remoteServer.getAdjustDeploymentTimestamp();
        remoteServer.setAdjustDeploymentTimestamp( adjustDemploymentTimestamp );
    }

    public void undo()
    {
        remoteServer.setAdjustDeploymentTimestamp( oldAdjustDemploymentTimestamp );
    }
}
