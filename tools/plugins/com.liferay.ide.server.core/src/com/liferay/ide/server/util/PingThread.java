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

	private boolean stop = false;
	private String url;
	protected IServer server;
	protected PortalServerBehavior behaviour;
    private long startedTime;
    private long defaultTimeout = 15 * 60 * 1000;
    private long timeout = 0;

	/**
	 * Create a new PingThread.
	 *
	 * @param server
	 * @param url
	 * @param maxPings the maximum number of times to try pinging, or -1 to continue forever
	 * @param behaviour
	 */
	public PingThread(IServer server, String url, PortalServerBehavior behaviour) {
        super();
        this.server = server;
        this.url = url;
        this.behaviour = behaviour;
        int serverStartTimeout = server.getStartTimeout();

        if( serverStartTimeout < defaultTimeout / 1000 )
        {
            this.timeout = defaultTimeout;
        }
        else
        {
            this.timeout = serverStartTimeout * 1000;
        }

        Thread t = new Thread( "Liferay Ping Thread" )
        {

            public void run()
            {
                startedTime = System.currentTimeMillis();
                ping();
            }
        };
        t.setDaemon( true );
        t.start();
    }

	/**
	 * Ping the server until it is started. Then set the server
	 * state to STATE_STARTED.
	 */
	protected void ping() {
	    long currentTime = 0;
		try {
			Thread.sleep(PING_DELAY);
		} catch (Exception e) {
		}
		while (!stop) {
			try {
                currentTime = System.currentTimeMillis();
                if( ( currentTime - startedTime ) > timeout )
                {
                    try
                    {
                        server.stop( false );
                    }
                    catch( Exception e )
                    {
                    }
                    stop = true;
                    break;
                }

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
				}
				behaviour.setServerStarted();
				stop = true;
			} catch (Exception e) {
				// pinging failed
				if (!stop) {
					try {
						Thread.sleep(PING_INTERVAL);
					} catch (InterruptedException e2) {
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
