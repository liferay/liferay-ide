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

package com.liferay.ide.server.util;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 * @author Terry Jia
 */
public class SocketUtil
{

    public static IStatus canConnect( String host, int port )
    {
        return canConnect( new Socket(), host, port );
    }

    public static IStatus canConnect( Socket socket, String host, int port )
    {
        IStatus status = null;

        InputStream in = null;

        try
        {
            InetSocketAddress address = new InetSocketAddress( host, port );
            InetSocketAddress local = new InetSocketAddress( 0 );
            socket.bind( local );
            socket.connect( address );
            in = socket.getInputStream();
            status = Status.OK_STATUS;
        }
        catch( Exception e )
        {
            status = LiferayServerCore.error( Msgs.notConnect );
            // e.printStackTrace();
        }
        finally
        {
            if( socket != null )
            {
                try
                {
                    socket.close();
                }
                catch( IOException e )
                {
                    // best effort
                }
            }

            if( in != null )
            {
                try
                {
                    in.close();
                }
                catch( Exception e )
                {
                    // best effort
                }
            }
        }

        return status;
    }

    public static IStatus canConnectProxy( String host, int port )
    {
        return canConnectProxy( new Socket(), host, port );
    }

    public static IStatus canConnectProxy( Socket socket, String host, int port )
    {
        IProxyService proxyService = LiferayCore.getProxyService();

        try
        {
            URI uri = new URI( "http://" + host + ":" + port ); //$NON-NLS-1$ //$NON-NLS-2$
            IProxyData[] proxyDataForHost = proxyService.select( uri );

            for( IProxyData data : proxyDataForHost )
            {
                if( data.getHost() != null )
                {
                    return SocketUtil.canConnect( socket, data.getHost(),data.getPort() );
                }
            }

            uri = new URI( "SOCKS://" + host + ":" + port ); //$NON-NLS-1$ //$NON-NLS-2$

            for( IProxyData data : proxyDataForHost )
            {
                if( data.getHost() != null )
                {
                    return SocketUtil.canConnect( socket, data.getHost(), data.getPort() );
                }
            }
        }
        catch( URISyntaxException e )
        {
            LiferayServerCore.logError( "Could not read proxy data", e ); //$NON-NLS-1$
        }

        return null;
    }

    public static boolean isPortAvailable( final String port )
    {
        ServerSocket serverSocket = null;

        try
        {
            serverSocket = new ServerSocket();

            serverSocket.bind( new InetSocketAddress( Integer.parseInt( port ) ) );

            return true;
        }
        catch( IOException e )
        {
        }
        finally
        {
            if( serverSocket != null )
            {
                try
                {
                    serverSocket.close();
                }
                catch( IOException e )
                {
                }
            }
        }

        return false;
    }

    private static class Msgs extends NLS
    {
        public static String notConnect;

        static
        {
            initializeMessages( SocketUtil.class.getName(), Msgs.class );
        }
    }
}
