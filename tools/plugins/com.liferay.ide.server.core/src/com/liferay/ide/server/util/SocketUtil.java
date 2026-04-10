/**
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
 */

package com.liferay.ide.server.util;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.IOException;
import java.io.InputStream;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class SocketUtil {

	public static IStatus canConnect(SSLSocket sslSocket, String host, String port) {
		IStatus status = null;

		InputStream in = null;

		try {
			InetSocketAddress address = new InetSocketAddress(host, Integer.valueOf(port));

			InetSocketAddress local = new InetSocketAddress(0);

			sslSocket.bind(local);

			sslSocket.connect(address);

			in = sslSocket.getInputStream();
			status = Status.OK_STATUS;
		}
		catch (IOException | NumberFormatException e) {
			status = LiferayServerCore.error(Msgs.notConnect);
		}
		finally {
			if (sslSocket != null) {
				try {
					sslSocket.close();
				}
				catch (IOException ioe) {

					// best effort

				}
			}

			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {

					// best effort

				}
			}
		}

		return status;
	}

	public static IStatus canConnect(String host, String port) throws IOException {
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

		return canConnect((SSLSocket)sslSocketFactory.createSocket(), host, port);
	}

	public static IStatus canConnectProxy(SSLSocket sslSocket, String host, String port) {
		IProxyService proxyService = LiferayCore.getProxyService();

		try {
			URI uri = new URI("http://" + host + ":" + port);

			IProxyData[] proxyDataForHost = proxyService.select(uri);

			for (IProxyData data : proxyDataForHost) {
				if (data.getHost() != null) {
					return canConnect(sslSocket, data.getHost(), String.valueOf(data.getPort()));
				}
			}

			uri = new URI("SOCKS://" + host + ":" + port);

			proxyDataForHost = proxyService.select(uri);

			for (IProxyData data : proxyDataForHost) {
				if (data.getHost() != null) {
					return canConnect(sslSocket, data.getHost(), String.valueOf(data.getPort()));
				}
			}
		}
		catch (URISyntaxException urise) {
			LiferayServerCore.logError("Could not read proxy data", urise);
		}

		return null;
	}

	public static IStatus canConnectProxy(String host, String port) throws IOException {
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

		return canConnectProxy((SSLSocket)sslSocketFactory.createSocket(), host, port);
	}

	public static boolean isPortAvailable(String port) {
		SSLServerSocket sslServerSocket = null;

		try {
			SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

			sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket();

			sslServerSocket.bind(new InetSocketAddress(Integer.parseInt(port)));

			return true;
		}
		catch (IOException | NumberFormatException e) {
		}
		finally {
			if (sslServerSocket != null) {
				try {
					sslServerSocket.close();
				}
				catch (IOException ioe) {
				}
			}
		}

		return false;
	}

	private static class Msgs extends NLS {

		public static String notConnect;

		static {
			initializeMessages(SocketUtil.class.getName(), Msgs.class);
		}

	}

}