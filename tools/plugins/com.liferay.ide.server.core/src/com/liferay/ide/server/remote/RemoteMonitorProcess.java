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

import com.liferay.ide.core.util.StringPool;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.internal.core.StreamsProxy;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class RemoteMonitorProcess extends Process implements IProcess {

	public RemoteMonitorProcess(IServer server, IServerManagerConnection connection, ILaunch launch) {
		this.server = server;
		remoteServer = (IRemoteServer)server.loadAdapter(IRemoteServer.class, null);
		remoteConnection = connection;
		this.launch = launch;
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	@Override
	public void destroy() {
		System.out.println("destroy");
	}

	@Override
	public int exitValue() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getAttribute(String key) {
		return null;
	}

	@Override
	public InputStream getErrorStream() {
		return new RemoteLogStream(server, remoteServer, remoteConnection, "error");
	}

	public int getExitValue() throws DebugException {
		return 0;
	}

	@Override
	public InputStream getInputStream() {
		return new RemoteLogStream(server, remoteServer, remoteConnection, "output");
	}

	public String getLabel() {
		if (label == null) {
			String host = null;
			String port = null;

			if (server != null) {
				host = server.getHost();
			}

			IRemoteServer wasServer = RemoteUtil.getRemoteServer(server);

			if (wasServer != null) {
				port = wasServer.getHTTPPort();
			}

			label = (host != null ? host : StringPool.EMPTY) + ":" + (port != null ? port : StringPool.EMPTY);
		}

		return label;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public OutputStream getOutputStream() {
		return null;
	}

	public IStreamsProxy getStreamsProxy() {
		if (streamsProxy == null) {
			streamsProxy = new StreamsProxy(this, "UTF-8");
		}

		return streamsProxy;
	}

	public boolean isTerminated() {
		if (remoteConnection == null) {
			return true;
		}

		return false;
	}

	public void setAttribute(String key, String value) {
	}

	public void terminate() throws DebugException {
		remoteConnection = null;

		// this.launch.removeProcess(this);

		DebugEvent[] events = {new DebugEvent(this, DebugEvent.TERMINATE)};

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.fireDebugEventSet(events);
	}

	@Override
	public int waitFor() throws InterruptedException {
		return 0;
	}

	protected String label;
	protected ILaunch launch;
	protected IServerManagerConnection remoteConnection;
	protected IRemoteServer remoteServer;
	protected IServer server;
	protected IStreamsProxy streamsProxy;

}