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

package com.liferay.ide.server.core.portal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IServer;

import com.liferay.ide.server.util.PingThread;

/**
 * Thread used to ping server to test when it is started.
 */
public class JBossPingThread extends PingThread {

	// delay before pinging starts
	private static final int PING_DELAY = 2000;

	// delay between pings
	private static final int PING_INTERVAL = 250;

	private boolean stop = false;
	private long startedTime;
	private long defaultTimeout = 15 * 60 * 1000;
	private long timeout = 0;
	private ProcessBuilder serverStateCommand = null;

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
	public JBossPingThread(IServer server, String url, PortalServerBehavior behaviour) {
		super(server, url, behaviour);
		int serverStartTimeout = server.getStartTimeout();

		if (serverStartTimeout < defaultTimeout / 1000) {
			this.timeout = defaultTimeout;
		} else {
			this.timeout = serverStartTimeout * 1000;
		}

		try {
			List<String> commands = new ArrayList<String>();
			IPath cmdDirectory = behaviour.getPortalRuntime().getAppServerDir().append("bin");
			String os = System.getProperty("os.name").toLowerCase();

			if (os.indexOf("win") >= 0) {
				commands.add(cmdDirectory.append("jboss-cli.bat").toOSString());
			} else if ((os.indexOf("nix") >= 0) || (os.indexOf("nux") >= 0)) {
				commands.add(cmdDirectory.append("jboss-cli.sh").toOSString());
			}

			commands.add("-c");
			commands.add("--commands=read-attribute server-state");

			serverStateCommand = new ProcessBuilder(commands);

			if (os.indexOf("win") >= 0) {
				Map<String, String> env = serverStateCommand.environment();
				env.put("NOPAUSE", "true");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread t = new Thread("Liferay Ping Thread") {

			public void run() {
				startedTime = System.currentTimeMillis();
				ping();
			}
		};
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Ping the server until it is started. Then set the server state to
	 * STATE_STARTED.
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
				if ((currentTime - startedTime) > timeout) {
					try {
						server.stop(false);
					} catch (Exception e) {
					}
					stop = true;
					break;
				}

				if (serverStateCommand != null) {
					Process serverStateProcess = serverStateCommand.start();
					try (BufferedReader reader = new BufferedReader(
							new InputStreamReader(serverStateProcess.getInputStream()))) {
						StringBuilder builder = new StringBuilder();
						String line = null;
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}
						String result = builder.toString();

						if (result != null && result.length() > 0) {
							if (!stop && result != null && result.equals("running")) {
								behaviour.setServerStarted();
								stop = true;
								Thread.sleep(200);
							}
						}
					}

					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// pinging failed
				if (!stop) {
					try {
						Thread.sleep(PING_INTERVAL);
					} catch (InterruptedException e2) {
						// ignore
					}
				}
				// e.printStackTrace();
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
