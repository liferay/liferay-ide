/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.portal.core.debug;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portal.core.PortalCore;
import com.liferay.ide.portal.core.debug.fm.FMDebugTarget;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.PortalLaunchParticipant;
import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.util.ServerUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class DebugPortalLaunchParticipant implements PortalLaunchParticipant
{

    private static final String FM_PARAMS = " -Dfreemarker.debug.password={0} -Dfreemarker.debug.port={1}"; //$NON-NLS-1$
    private static final String STOP_SERVER = "stop-server"; //$NON-NLS-1$

    private String fmDebugPort;
    private String saveLaunchMode;

    public ISourceLookupParticipant[] getPortalSourceLookupParticipants()
    {
        return new ISourceLookupParticipant[] { new PortalSourceLookupParticipant() };
    }

    private String getValidatedDebugPort()
    {
        // This method will firstly do the validation on the customized port,
        // if the port is not valid, it will search the whole extension ports from port 1025 to 65535
        // to find another valid port, if no port is found, it returns null.
        int port = Integer.parseInt( PortalCore.getPreference( PortalCore.PREF_FM_DEBUG_PORT ) );

        final int lowerBound = 1025;
        final int upperBound = 65535;

        if( port < lowerBound || port > upperBound )
        {
            port = lowerBound;
        }

        while( port <= upperBound )
        {
            try
            {
                ServerSocket server = new ServerSocket();
                server.bind( new InetSocketAddress( port ) );
                server.close();

                break;
            }
            catch( IOException e )
            {
                port++;
            }
        }

        if( port <= upperBound )
        {
            return String.valueOf( port );
        }

        return null;
    }

    public void portalPostLaunch(
        ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        final IServer server = org.eclipse.wst.server.core.ServerUtil.getServer( configuration );
        final IServerManagerConnection connection = ServerUtil.getServerManagerConnection( server, monitor );

        if( connection != null )
        {
            try
            {
                final String fmDebugPassword = connection.getFMDebugPassword();
                final int fmDebugPort = connection.getFMDebugPort();

                if( fmDebugPassword != null && fmDebugPort != -1 )
                {
                    launch.setAttribute( PortalCore.PREF_FM_DEBUG_PASSWORD, fmDebugPassword );
                    launch.setAttribute( PortalCore.PREF_FM_DEBUG_PORT, Integer.toString( fmDebugPort ) );

                    final IDebugTarget target = new FMDebugTarget( server.getHost(), launch, launch.getProcesses()[0] );

                    launch.addDebugTarget( target );
                }
            }
            catch( APIException e )
            {
                LiferayServerCore.logError( "Unable to determine remote freemarker debugger connection info.", e );
            }
        }

        this.saveLaunchMode = null;

        final String stopServer = configuration.getAttribute( STOP_SERVER, "false" );

        if( ILaunchManager.DEBUG_MODE.equals( mode ) && "false".equals( stopServer ) )
        {
            if( this.fmDebugPort != null )
            {
                launch.setAttribute( PortalCore.PREF_FM_DEBUG_PORT, this.fmDebugPort );
                this.fmDebugPort = null;

                final IDebugTarget target = new FMDebugTarget( server.getHost(), launch, launch.getProcesses()[0] );
                launch.addDebugTarget( target );
            }
            else
            {
                PortalCore.logError( "Launch freemarker port is invalid." ); //$NON-NLS-1$
            }
        }
    }

    public void portalPreLaunch(
        ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        this.fmDebugPort = getValidatedDebugPort();
        this.saveLaunchMode = mode;
    }

    public String provideVMArgs( ILaunchConfiguration configuration )
    {
        String retval = null;

        try
        {
            String stopServer = configuration.getAttribute( STOP_SERVER, "false" );

            if( ILaunchManager.DEBUG_MODE.equals( saveLaunchMode ) && "false".equals( stopServer ) )
            {
                final IServer server = org.eclipse.wst.server.core.ServerUtil.getServer( configuration );

                final ILiferayRuntime liferayRuntime =
                    (ILiferayRuntime) server.getRuntime().loadAdapter( ILiferayRuntime.class, null );

                final Version version = new Version( liferayRuntime.getPortalVersion() );

                if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
                {
                    if( this.fmDebugPort != null )
                    {
                        retval =
                            NLS.bind(
                                FM_PARAMS, PortalCore.getPreference( PortalCore.PREF_FM_DEBUG_PASSWORD ),
                                this.fmDebugPort );
                    }
                    else
                    {
                        PortalCore.logError( "The freemarker debug port is invalid." ); //$NON-NLS-1$
                    }
                }
            }
        }
        catch( CoreException e )
        {
        }

        return retval;
    }
}
