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

package com.liferay.ide.server.core.portal.docker;

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.debug.core.model.IStreamMonitor;

/**
 * @author Simon Jiang
 */
public class PortalDockerServerStreamsProxy implements IPortalDockerStreamsProxy {

	public IStreamMonitor getErrorStreamMonitor() {
		return sysErr;
	}

	public IStreamMonitor getOutputStreamMonitor() {
		return sysOut;
	}

	public boolean isMonitorStopping() {
		return _monitorStopping;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void terminate() {
		try {
			isTerminated = true;
			//			_attachContainerCmd.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(String input) throws IOException {
	}

	protected void readToNow(BufferedReader br) throws IOException {
		String s = "";

		while (s != null) {
			s = br.readLine();
		}
	}

	protected void setIsMonitorStopping(boolean curIsMonitorStopping) {
		_monitorStopping = curIsMonitorStopping;
	}

	protected void startMonitoring(PortalDockerServer portalServer) {
		if (_streamThread != null) {
			return;
		}

		_streamThread = new Thread("Liferay Portal Docker Server IO Monitor Stream") {

			public void run() {

				/**
				 * IDockerSupporter dockerSupporter = LiferayServerCore.getDockerSupporter();
				 *
				 * if (dockerSupporter == null) { return; }
				 *
				 * try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
				 * _attachContainerCmd =
				 * dockerClient.attachContainerCmd(portalServer.getContainerId());
				 *
				 * _attachContainerCmd.withFollowStream(true);
				 * _attachContainerCmd.withLogs(false); _attachContainerCmd.withStdOut(true);
				 * _attachContainerCmd.withStdErr(true);
				 *
				 * LiferayAttachCallback liferayAttachCallback = new
				 * LiferayAttachCallback(sysOut); isTerminated = false;
				 * _attachContainerCmd.exec(liferayAttachCallback);
				 *
				 * liferayAttachCallback.awaitCompletion(); } catch (Exception ie) {
				 * LiferayServerCore.logError(ie); }
				 */
			}

		};

		_streamThread.setPriority(1);
		_streamThread.setDaemon(true);
		_streamThread.start();
	}

	protected boolean isTerminated = false;
	protected PortalDockerServerOutputStreamMonitor sysErr;
	protected PortalDockerServerOutputStreamMonitor sysOut;

	//	private AttachContainerCmd _attachContainerCmd;
	private boolean _monitorStopping = false;
	private Thread _streamThread;

	/**
	 * private class LiferayAttachCallback extends AttachContainerResultCallback {
	 *
	 * public LiferayAttachCallback(PortalDockerServerOutputStreamMonitor sysOut) {
	 * _sysOut = sysOut; }
	 *
	 * @Override public void onNext(Frame item) { try (InputStreamReader reader =
	 * new InputStreamReader(new ByteArrayInputStream(item.getPayload()))) { int
	 * read = 0;
	 *
	 * final int buffer_size = 8192;
	 *
	 * char[] chars = new char[buffer_size];
	 *
	 * while (read >= 0) { try { read = reader.read(chars);
	 *
	 * if (read > 0) { String text = new String(chars, 0, read);
	 *
	 * synchronized (this) { _sysOut.append(text); } } } catch (IOException ioe) { }
	 * catch (NullPointerException npe) { } } } catch (IOException ioe) { } }
	 *
	 * private PortalDockerServerOutputStreamMonitor _sysOut;
	 *
	 * }
	 */

}