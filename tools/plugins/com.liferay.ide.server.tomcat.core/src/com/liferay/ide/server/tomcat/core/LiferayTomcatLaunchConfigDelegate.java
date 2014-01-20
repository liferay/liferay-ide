/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.LiferayDebugCore;
import com.liferay.ide.debug.core.fm.FMDebugTarget;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.PortalSourceLookupDirector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jst.server.tomcat.core.internal.TomcatLaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Tao Tao
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatLaunchConfigDelegate extends TomcatLaunchConfigurationDelegate
{
    private static final String STOP_SERVER = "stop-server"; //$NON-NLS-1$
    private static final String FALSE = "false"; //$NON-NLS-1$
    private static final String FM_PARAMS = " -Dfreemarker.debug.password={0} -Dfreemarker.debug.port={1}"; //$NON-NLS-1$

    private String saveLaunchMode;
    private String fmDebugPort;

    @Override
    public String getVMArguments( ILaunchConfiguration configuration ) throws CoreException
    {
        String retval = super.getVMArguments( configuration );

        String stopServer = configuration.getAttribute( STOP_SERVER, FALSE );

        if( ILaunchManager.DEBUG_MODE.equals( saveLaunchMode ) && FALSE.equals( stopServer ) )
        {
            try
            {
                final IServer server = ServerUtil.getServer( configuration );

                final ILiferayRuntime liferayRuntime =
                    (ILiferayRuntime) server.getRuntime().loadAdapter( ILiferayRuntime.class, null );

                final Version version = new Version( liferayRuntime.getPortalVersion() );

                if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
                {
                    if( this.fmDebugPort != null )
                    {
                        retval +=
                            NLS.bind(
                                FM_PARAMS, LiferayDebugCore.getPreference( LiferayDebugCore.PREF_FM_DEBUG_PASSWORD ),
                                this.fmDebugPort );
                    }
                    else
                    {
                        LiferayDebugCore.logError( "The freemarker debug port is invalid." ); //$NON-NLS-1$
                    }
                }
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    @Override
    public void launch(
        final ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        if( ILaunchManager.DEBUG_MODE.equals( mode ) )
        {
            final PortalSourceLookupDirector sourceLocator =
                new PortalSourceLookupDirector( configuration, LiferayTomcatSourcePathComputer.ID );
            sourceLocator.configureLaunch( launch );
        }

        this.fmDebugPort = getValidatedDebugPort();
        this.saveLaunchMode = mode;
        super.launch( configuration, mode, launch, monitor );
        this.saveLaunchMode = null;

        final String stopServer = configuration.getAttribute( STOP_SERVER, FALSE );

        if( ILaunchManager.DEBUG_MODE.equals( mode ) && FALSE.equals( stopServer ) )
        {


            if( this.fmDebugPort != null )
            {
                launch.setAttribute( LiferayDebugCore.PREF_FM_DEBUG_PORT, this.fmDebugPort );
                this.fmDebugPort = null;

                final IServer server = ServerUtil.getServer( configuration );

                final IDebugTarget target = new FMDebugTarget( server.getHost(), launch, launch.getProcesses()[0] );
                launch.addDebugTarget( target );
            }
            else
            {
                LiferayDebugCore.logError( "Launch freemarker port is invalid." ); //$NON-NLS-1$
            }
        }
    }

    private String getValidatedDebugPort()
    {
        // This method will firstly do the validation on the customized port,
        // if the port is not valid, it will search the whole extension ports from port 1025 to 65535
        // to find another valid port, if no port is found, it returns null.
        int port = Integer.parseInt( LiferayDebugCore.getPreference( LiferayDebugCore.PREF_FM_DEBUG_PORT ) );

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
}
