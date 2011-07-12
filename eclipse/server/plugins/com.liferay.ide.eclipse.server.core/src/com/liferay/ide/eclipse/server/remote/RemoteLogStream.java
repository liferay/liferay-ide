/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.remote;

import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
public class RemoteLogStream extends BufferedInputStream {

	@SuppressWarnings( "deprecation" )
	public static final IEclipsePreferences _defaultPrefs =
		new DefaultScope().getNode( LiferayServerCorePlugin.PLUGIN_ID );

	public static final long LOG_QUERY_RANGE = _defaultPrefs.getLong("log.query.range", 51200);

	public final static long OUTPUT_MONITOR_DELAY = _defaultPrefs.getLong("output.monitor.delay", 1000);

	protected static URL createBaseUrl(
		IServer server, IRemoteServer remoteServer, IRemoteConnection connection, String log ) {

		try {
			return new URL( connection.getManagerURI() + "/log/" + log );
		}
		catch (MalformedURLException e) {
		}

		return null;
	}

	protected static InputStream createInputStream(
		IServer server, IRemoteServer remoteServer, IRemoteConnection connection, String log ) {

		try {
			URL url = createBaseUrl( server, remoteServer, connection, log );

			URLConnection conn = null;

			// if (websphereServer.getSecurityEnabled()) {
			// String username = websphereServer.getUsername();
			// String password = websphereServer.getPassword();
			//
			// String authString = username + ":" + password;
			// byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			// String authStringEnc = new String(authEncBytes);
			//
			// conn = url.openConnection();
			// conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			// }
			// else {
				conn = url.openConnection();
			// }

			return conn.getInputStream();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected URL baseUrl = null;

	protected String log;

	protected long range = 0;

	public RemoteLogStream( IServer server, IRemoteServer remoteServer, IRemoteConnection connection, String log ) {
		super( createInputStream( server, remoteServer, connection, log ), 8192 );
		this.baseUrl = createBaseUrl( server, remoteServer, connection, log );
		this.log = log;
	}

	@Override
	public int read(byte[] b)
		throws IOException {

		int read = super.read(b);

		if (read < 1) {
			waitOnNewInput();

			read = super.read(b);

			range += read;
		}

		return read;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len)
		throws IOException {
		int read = super.read(b, off, len);

		if (read < 1) {
			waitOnNewInput();
			read = super.read(b, off, len);
			range += read;
		}

		return read;
	}

	protected void waitOnNewInput()
		throws IOException {
		// previous input stream was empty, so we need to move the range
		this.in.close();

		// peek at the new stream
		boolean goodUrl = false;

		while (!goodUrl) {
			URL newUrl =
				new URL(baseUrl, log + "?" + baseUrl.getQuery() + "&rangeStart=" + range + "&rangeEnd=" +
					(range + LOG_QUERY_RANGE));

			try {
				goodUrl = urlPeek(newUrl);
			}
			catch (Exception e) {
				// failed to get a new good url, try again next time
			}

			if (goodUrl) {
				this.in = newUrl.openStream();
				return;
			}

			try {
				Thread.sleep(OUTPUT_MONITOR_DELAY);
			}
			catch (InterruptedException e) {
			}
		}
	}

	boolean urlPeek(URL url)
		throws IOException {
		byte[] buf = new byte[256];

		int bufRead = new BufferedInputStream(url.openStream(), 256).read(buf);

		if (bufRead != -1) {
			String peek = new String(buf);

			if (peek != null && (!peek.contains("Error 416: Invalid Range values."))) {
				return true;
			}
		}

		return false;
	}
}
