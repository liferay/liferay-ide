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

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

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
     * @param deployDir
     *            deployment directory to set
     */
    public SetExternalPropertiesCommand( IServerWorkingCopy server, String externalProperties )
    {
        super( server, Msgs.setExternalProperties );
        this.externalProperties = externalProperties;
    }

    /**
     * Execute setting the external properties
     */
    public void execute()
    {
        oldExternalProperties =  ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getExternalProperties();
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setExternalProperties( externalProperties );
    }

    /**
     * Restore prior external properties
     */
    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setExternalProperties( oldExternalProperties );
    }

    private static class Msgs extends NLS
    {
        public static String setExternalProperties;

        static
        {
            initializeMessages( SetExternalPropertiesCommand.class.getName(), Msgs.class );
        }
    }
}
