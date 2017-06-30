/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package com.liferay.ide.server.util;

import com.liferay.ide.server.core.portal.PortalServerBehavior;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.wst.server.core.IServer;
/**
 * Thread used to ping server to test when it is started.
 */
public class PingThread {
	// delay before pinging starts
	private static final int PING_DELAY = 2000;

	// delay between pings
	private static final int PING_INTERVAL = 250;

	// maximum number of pings before giving up
	private int maxPings;

	private boolean stop = false;
	private String url;
	private IServer server;
	private PortalServerBehavior behaviour;

	/**
	 * Create a new PingThread.
	 *
	 * @param server
	 * @param url
	 * @param maxPings the maximum number of times to try pinging, or -1 to continue forever
	 * @param behaviour
	 */
	public PingThread(IServer server, String url, int maxPings, PortalServerBehavior behaviour) {
		super();
		this.server = server;
		this.url = url;
		this.maxPings = maxPings;
		this.behaviour = behaviour;
		Thread t = new Thread("Liferay Ping Thread") {
			public void run() {
				ping();
			}
		};
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Ping the server until it is started. Then set the server
	 * state to STATE_STARTED.
	 */
	protected void ping() {
		int count = 0;
		try {
			Thread.sleep(PING_DELAY);
		} catch (Exception e) {
			// ignore
		}
		while (!stop) {
			try {
				if (count == maxPings) {
					try {
						server.stop(false);
					} catch (Exception e) {
					}
					stop = true;
					break;
				}
				count++;

				URL pingUrl = new URL(url);
				URLConnection conn = pingUrl.openConnection();

				((HttpURLConnection)conn).setInstanceFollowRedirects(false);
				int code = ((HttpURLConnection)conn).getResponseCode();

				// ping worked - server is up
				if (!stop && code != 404) {
					Thread.sleep(200);
					behaviour.setServerStarted();
					stop = true;
				}
				Thread.sleep(1000);
			} catch (FileNotFoundException fe) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					// ignore
				}
				behaviour.setServerStarted();
				stop = true;
			} catch (Exception e) {
				// pinging failed
				if (!stop) {
					try {
						Thread.sleep(PING_INTERVAL);
					} catch (InterruptedException e2) {
						// ignore
					}
				}
			}
		}
	}

	/**
	 * Tell the pinging to stop.
	 */
	public void stop() {
		stop = true;
	}
}
