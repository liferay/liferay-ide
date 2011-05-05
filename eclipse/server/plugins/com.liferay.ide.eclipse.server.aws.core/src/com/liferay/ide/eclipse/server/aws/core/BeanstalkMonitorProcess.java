/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.aws.core;

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
public class BeanstalkMonitorProcess extends Process implements IProcess {

	// protected IWebsphereAdminService adminService;
	protected Object adminService;
	protected String label;
	protected ILaunch launch;
	protected IServer server;
	protected IStreamsProxy streamsProxy;
	protected IBeanstalkServer beanstalkServer;

	public BeanstalkMonitorProcess(IServer server, Object service, ILaunch launch) {
		this.server = server;
		this.beanstalkServer = (IBeanstalkServer) server.loadAdapter(IBeanstalkServer.class, null);
		this.adminService = service;
		this.launch = launch;
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	@Override
	public void destroy() {
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
		// return (/* IProcess.ATTR_PROCESS_TYPE.equals(key) || */IProcess.ATTR_PROCESS_LABEL.equals(key))
		// ? "WebSphere Admin Server Monitor" : null;
		return null;
	}

	@Override
	public InputStream getErrorStream() {
		// return new WebsphereLogStream(server, websphereServer, adminService, "syserr");
		return null;
	}

	public int getExitValue()
		throws DebugException {

		return 0;
	}

	@Override
	public InputStream getInputStream() {
		// return new WebsphereLogStream(server, websphereServer, adminService, "sysout");
		return null;
	}

	public String getLabel() {
		if (this.label == null) {
			String host = null;
			String port = "80";

			if (server != null) {
				host = server.getHost();
			}

			// IWebsphereServer wasServer = WebsphereUtil.getWebsphereServer(server);

			// if (wasServer != null) {
			// port = wasServer.getSOAPPort();
			// }

			this.label = (host != null ? host : "") + ":" + (port != null ? port : "");
		}

		return this.label;
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
		return adminService == null; // || (!adminConnection.isAlive().isOK());
	}

	public void setAttribute(String key, String value) {
	}

	public void terminate()
		throws DebugException {
		adminService = null;
		// this.launch.removeProcess(this);

		DebugEvent[] events = {
			new DebugEvent(this, DebugEvent.TERMINATE)
		};

		DebugPlugin.getDefault().fireDebugEventSet(events);
	}

	@Override
	public int waitFor()
		throws InterruptedException {

		return 0;
	}

}
