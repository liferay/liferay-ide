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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 * @author Tao Tao
 * @author Simon Jiang
 */
public class RemoteLogStream extends BufferedInputStream {

	public static final IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode(LiferayServerCore.PLUGIN_ID);

	public static final long LOG_QUERY_RANGE = defaultPrefs.getLong("log.query.range", 51200);

	public static final long OUTPUT_MONITOR_DELAY = defaultPrefs.getLong("output.monitor.delay", 1000);

	public RemoteLogStream(
		IServer server, IRemoteServer remoteServer, IServerManagerConnection connection, String log) {

		super(createInputStream(server, remoteServer, connection, log), 8192);

		baseUrl = createBaseUrl(server, remoteServer, connection, log);

		this.log = log;
		this.connection = connection;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = super.read(b);

		if (read < 1) {
			waitOnNewInput();

			read = super.read(b);
		}

		range += read;

		return read;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		int read = super.read(b, off, len);

		if (read < 1) {
			waitOnNewInput();
			read = super.read(b, off, len);
		}

		range += read;

		return read;
	}

	protected static URL createBaseUrl(
		IServer server, IRemoteServer remoteServer, IServerManagerConnection connection, String log) {

		try {
			return new URL(getLogURI(connection, log) + getFormatQuery());
		}
		catch (MalformedURLException murle) {
		}

		return null;
	}

	protected static InputStream createInputStream(
		IServer server, IRemoteServer remoteServer, IServerManagerConnection connection, String log) {

		try {
			URL url = createBaseUrl(server, remoteServer, connection, log);

			return openInputStream(connection, url);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected static String getFormatQuery() {
		return "?format=raw";
	}

	protected static String getLogURI(IServerManagerConnection connection, String log) {
		return connection.getManagerURI() + "/server/log/" + log;
	}

	protected static InputStream openInputStream(IServerManagerConnection remote, URL url) throws IOException {
		String username = remote.getUsername();
		String password = remote.getPassword();

		String authString = username + ":" + password;

		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());

		String authStringEnc = new String(authEncBytes);

		IProxyService proxyService = LiferayCore.getProxyService();

		try {
			URI uri = new URI("HTTP://" + url.getHost() + ":" + url.getPort());

			IProxyData[] proxyDataForHost = proxyService.select(uri);

			for (IProxyData data : proxyDataForHost) {
				if (data.getHost() != null) {
					System.setProperty("http.proxyHost", data.getHost());
					System.setProperty("http.proxyPort", String.valueOf(data.getPort()));

					break;
				}
			}

			uri = new URI("SOCKS://" + url.getHost() + ":" + url.getPort());

			proxyDataForHost = proxyService.select(uri);

			for (IProxyData data : proxyDataForHost) {
				if (data.getHost() != null) {
					System.setProperty("socksProxyHost", data.getHost());
					System.setProperty("socksProxyPort", String.valueOf(data.getPort()));

					break;
				}
			}
		}
		catch (URISyntaxException urise) {
			LiferayServerCore.logError("Could not read proxy data", urise);
		}

		URLConnection conn = url.openConnection();

		conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

		Authenticator.setDefault(null);
		conn.setAllowUserInteraction(false);

		return conn.getInputStream();
	}

	protected void waitOnNewInput() throws IOException {

		// previous input stream was empty, so we need to move the range

		this.in.close();

		// peek at the new stream

		boolean goodUrl = false;

		while (!goodUrl) {
			URL newUrl = new URL(getLogURI(connection, log) + "/" + range + getFormatQuery());

			try {
				goodUrl = _urlPeek(newUrl);
			}
			catch (Exception e) {

				// failed to get a new good url, try again next time

			}

			if (goodUrl) {
				in = openInputStream(connection, newUrl);

				return;
			}

			try {
				Thread.sleep(OUTPUT_MONITOR_DELAY);
			}
			catch (InterruptedException ie) {
			}
		}
	}

	protected URL baseUrl = null;
	protected IServerManagerConnection connection;
	protected String log;
	protected long range = 0;

	private boolean _urlPeek(URL url) throws IOException {
		byte[] buf = new byte[256];

		try (BufferedInputStream buffer = new BufferedInputStream(openInputStream(connection, url), 256)) {
			int bufRead = buffer.read(buf);

			if (bufRead != -1) {
				String peek = new String(buf);

				if ((peek != null) && !peek.contains("Error 416: Invalid Range values.")) {
					return true;
				}
			}
		}

		return false;
	}

}