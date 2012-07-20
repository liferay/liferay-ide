/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;

/**
 * @author Greg Amerson
 */
public class SetHttpPortCommand extends RemoteServerCommand
{

    protected String oldHttpPort;
    protected String httpPort;

    public SetHttpPortCommand( IRemoteServerWorkingCopy server, String httpPort )
    {
        super( server, "Set Http Port" );
        this.httpPort = httpPort;
    }

    public void execute()
    {
        oldHttpPort = server.getHTTPPort();
        server.setHTTPPort( httpPort );
    }

    public void undo()
    {
        server.setHTTPPort( oldHttpPort );
    }
}
