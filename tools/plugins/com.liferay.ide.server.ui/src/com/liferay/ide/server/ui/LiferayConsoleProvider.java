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

package com.liferay.ide.server.ui;

import com.liferay.ide.server.core.portal.LiferayServerLogFileStreamsProxy;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalJBossBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * @author Simon Jiang
 */
public class LiferayConsoleProvider extends ConsoleColorProvider {

	@Override
	public void connect(IProcess process, IConsole console) {
		super.connect(process, console);

		try {
			ILaunch launch = process.getLaunch();

			ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();

			IServer server = ServerUtil.getServer(launchConfiguration);

			if (IServer.STATE_STOPPED == server.getServerState()) {
				return;
			}

			IRuntime runtime = server.getRuntime();

			if (runtime == null) {
				return;
			}

			PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, null);

			if (portalRuntime == null) {
				return;
			}

			PortalBundle portalBundle = portalRuntime.getPortalBundle();

			if ((portalBundle != null) && (portalBundle instanceof PortalJBossBundle)) {
				_streamsProxy = new LiferayServerLogFileStreamsProxy(portalRuntime, launch);

				_processListener = new IDebugEventSetListener() {

					@Override
					public void handleDebugEvents(DebugEvent[] events) {
						if (events != null) {
							for (DebugEvent event : events) {
								if ((process != null) && process.equals(event.getSource()) &&
									(event.getKind() == DebugEvent.TERMINATE)) {

									_streamsProxy.terminate();
								}
							}
						}
					}

				};

				DebugPlugin debugPlugin = DebugPlugin.getDefault();

				debugPlugin.addDebugEventListener(_processListener);

				console.connect(_streamsProxy.getOutputStreamMonitor(), IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM);
			}
		}
		catch (Exception e) {
			LiferayServerUI.logError(e);
		}
	}

	@Override
	public void disconnect() {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		debugPlugin.removeDebugEventListener(_processListener);
	}

	private transient IDebugEventSetListener _processListener = null;
	private LiferayServerLogFileStreamsProxy _streamsProxy = null;

}