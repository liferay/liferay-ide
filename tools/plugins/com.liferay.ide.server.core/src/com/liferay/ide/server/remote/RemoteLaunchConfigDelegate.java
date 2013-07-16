/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.debug.core.LiferayDebugCore;
import com.liferay.ide.debug.core.fm.FMDebugTarget;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class RemoteLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate
{
    public static final String SERVER_ID = "server-id"; //$NON-NLS-1$

    @SuppressWarnings( { "rawtypes", "unchecked", "deprecation" } )
    protected void debugLaunch(
        IServer server, ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {

        if( monitor == null )
        {
            monitor = new NullProgressMonitor();
        }

        // setup the run launch so we get console monitor
        runLaunch( server, configuration, launch, monitor );

        String connectorId = getVMConnectorId( configuration );

        IVMConnector connector = null;

        if( connectorId == null )
        {
            connector = JavaRuntime.getDefaultVMConnector();
        }
        else
        {
            connector = JavaRuntime.getVMConnector( connectorId );
        }

        if( connector == null )
        {
            abort(
                "Debugging connector not specified.", null, //$NON-NLS-1$
                IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE );
        }

        Map connectMap = configuration.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null );

        int connectTimeout = JavaRuntime.getPreferences().getInt( JavaRuntime.PREF_CONNECT_TIMEOUT );

        connectMap.put( "timeout", StringPool.EMPTY + connectTimeout ); //$NON-NLS-1$

        IServerManagerConnection connection = getServerManagerConnection( server, monitor );

        if( connection != null )
        {
            try
            {
                String fmDebugPassword = connection.getFMDebugPassword();
                int fmDebugPort = connection.getFMDebugPort();

                if( fmDebugPassword != null && fmDebugPort != -1 )
                {
                    launch.setAttribute( LiferayDebugCore.PREF_FM_DEBUG_PASSWORD, fmDebugPassword );
                    launch.setAttribute( LiferayDebugCore.PREF_FM_DEBUG_PORT, Integer.toString( fmDebugPort ) );
                    final IDebugTarget target = new FMDebugTarget( server.getHost(), launch, launch.getProcesses()[0] );
                    launch.addDebugTarget( target );
                }
            }
            catch( APIException e )
            {
            }
        }

        // check for cancellation
        if( monitor.isCanceled() )
        {
            return;
        }

        // set the default source locator if required
        setDefaultSourceLocator( launch, configuration );

        if( !launch.isTerminated() )
        {
            // connect to remote VM
            connector.connect( connectMap, monitor, launch );
        }

        // check for cancellation
        if( monitor.isCanceled() || launch.isTerminated() )
        {
            IDebugTarget[] debugTargets = launch.getDebugTargets();
            for( int i = 0; i < debugTargets.length; i++ )
            {
                IDebugTarget target = debugTargets[i];
                if( target.canDisconnect() )
                {
                    target.disconnect();
                }
            }
            return;
        }

        monitor.done();
    }

    public IServerManagerConnection getServerManagerConnection( IServer server, IProgressMonitor monitor )
    {
        IServerManagerConnection connection =
            LiferayServerCore.getRemoteConnection( (IRemoteServer) server.loadAdapter( IRemoteServer.class, monitor ) );

        return connection;
    }

    public void launch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {

        String serverId = configuration.getAttribute( SERVER_ID, StringPool.EMPTY );
        IServer server = ServerCore.findServer( serverId );

        if( server == null )
        {
            // server has been deleted
            launch.terminate();
            return;
        }

        int state = server.getServerState();

        if( state != IServer.STATE_STARTED )
        {
            throw new CoreException(
                LiferayServerCore.createErrorStatus( Msgs.serverNotRunning ) );
        }

        if( ILaunchManager.RUN_MODE.equals( mode ) )
        {
            runLaunch( server, configuration, launch, monitor );
        }
        else if( ILaunchManager.DEBUG_MODE.equals( mode ) )
        {
            debugLaunch( server, configuration, launch, monitor );
        }
        else
        {
            throw new CoreException( LiferayServerCore.createErrorStatus( "Profile mode is not supported." ) ); //$NON-NLS-1$
        }
    }

    protected void runLaunch(
        IServer server, ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {

        IServerManagerConnection connection = getServerManagerConnection( server, monitor );

        RemoteMonitorProcess process = new RemoteMonitorProcess( server, connection, launch );

        launch.addProcess( process );
    }

    private static class Msgs extends NLS
    {
        public static String serverNotRunning;

        static
        {
            initializeMessages( RemoteLaunchConfigDelegate.class.getName(), Msgs.class );
        }
    }
}
