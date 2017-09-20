/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.core.LiferayServerCommand;
import com.liferay.ide.server.core.portal.IPortalBundleConfiguration;
import com.liferay.ide.server.core.portal.LiferayServerPort;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import java.util.Iterator;

/**
 * Command to change the configuration port.
 */
public class ModifyPortCommand extends LiferayServerCommand
{

    protected String id;
    protected int port;
    protected int oldPort;
    IPortalBundleConfiguration bundleConfiguration;
    PortalServerDelegate serverDelgate;

    public ModifyPortCommand(
        IPortalBundleConfiguration bundleConfiguration, PortalServerDelegate server, String id, int port )
    {
        super( server, "" );
        this.serverDelgate = server;
        this.bundleConfiguration = bundleConfiguration;
        this.id = id;
        this.port = port;
    }

    /**
     * Execute the command.
     */
    public void execute()
    {
        // find old port number
        Iterator<LiferayServerPort> iterator = serverDelgate.getLiferayServerPorts().iterator();
        while( iterator.hasNext() )
        {
            LiferayServerPort temp = (LiferayServerPort) iterator.next();
            if( id.equals( temp.getId() ) )
                oldPort = temp.getPort();
        }

        // make the change
        bundleConfiguration.modifyServerPort( id, port );
    }

    /**
     * Undo the command.
     */
    public void undo()
    {
        bundleConfiguration.modifyServerPort( id, oldPort );
    }
}
