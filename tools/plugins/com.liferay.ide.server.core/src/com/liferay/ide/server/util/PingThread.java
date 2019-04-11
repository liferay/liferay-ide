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

import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.io.FileNotFoundException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class PingThread {

	// delay before pinging starts

	/**
	 * Create a new PingThread.
	 *
	 * @param server
	 * @param url
	 * @param maxPings
	 *            the maximum number of times to try pinging, or -1 to continue
	 *            forever
	 * @param behaviour
	 */
	public PingThread(IServer server, String url, int maxPings, PortalServerBehavior behaviour) {
		_server = server;
		_url = url;
		_maxPings = maxPings;
		_behaviour = behaviour;

		Thread t = new Thread("Liferay Ping Thread") {

			public void run() {
				ping();
			}

		};

		t.setDaemon(true);

		t.start();
	}

	// delay between pings

	/**
	 * Tell the pinging to stop.
	 */
	public void stop() {
		_stop = true;
	}

	// maximum number of pings before giving up

	/**
	 * Ping the server until it is started. Then set the server state to
	 * STATE_STARTED.
	 */
	protected void ping() {
		int count = 0;

		try {
			Thread.sleep(_PING_DELAY);
		}
		catch (Exception e) {

			// ignore

		}

		while (!_stop) {
			try {
				if (count == _maxPings) {
					try {
						_server.stop(false);
					}
					catch (Exception e) {
					}

					_stop = true;

					break;
				}

				count++;

				URL pingUrl = new URL(_url);

				URLConnection conn = pingUrl.openConnection();

				((HttpURLConnection)conn).setInstanceFollowRedirects(false);

				int code = ((HttpURLConnection)conn).getResponseCode();

				// ping worked - server is up

				if (!_stop && (code != 404)) {
					Thread.sleep(200);
					_behaviour.setServerStarted();
					_stop = true;
				}

				Thread.sleep(1000);
			}
			catch (FileNotFoundException fnfe) {
				try {
					Thread.sleep(200);
				}
				catch (Exception e) {

					// ignore

				}

				_behaviour.setServerStarted();
				_stop = true;
			}
			catch (Exception e) {

				// pinging failed

				if (!_stop) {
					try {
						Thread.sleep(_PING_INTERVAL);
					}
					catch (InterruptedException ie) {

						// ignore

					}
				}
			}
		}
	}

	private static final int _PING_DELAY = 2000;

	private static final int _PING_INTERVAL = 250;

	private PortalServerBehavior _behaviour;
	private int _maxPings;
	private IServer _server;
	private boolean _stop = false;
	private String _url;

}