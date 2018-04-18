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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.server.core.ILiferayServer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.tm.terminal.view.core.interfaces.ITerminalService.Done;
import org.eclipse.tm.terminal.view.core.interfaces.constants.ITerminalsConnectorConstants;
import org.eclipse.tm.terminal.view.ui.interfaces.ILauncherDelegate;
import org.eclipse.tm.terminal.view.ui.launcher.LauncherDelegateManager;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class OpenGogoShellAction extends AbstractServerRunningAction {

	public OpenGogoShellAction() {
	}

	public void run(IAction action) {
		if (selectedServer != null) {
			String host = selectedServer.getHost();

			LauncherDelegateManager launchDelegateManager = LauncherDelegateManager.getInstance();

			ILauncherDelegate delegate = launchDelegateManager.getLauncherDelegate(
				"org.eclipse.tm.terminal.connector.telnet.launcher.telnet", false);

			Map<String, Object> properties = new HashMap<>();

			properties.put(
				ITerminalsConnectorConstants.PROP_TERMINAL_CONNECTOR_ID,
				"org.eclipse.tm.terminal.connector.telnet.TelnetConnector");
			properties.put(
				ITerminalsConnectorConstants.PROP_DELEGATE_ID,
				"org.eclipse.tm.terminal.connector.telnet.launcher.telnet");
			properties.put(ITerminalsConnectorConstants.PROP_IP_HOST, host);
			properties.put(ITerminalsConnectorConstants.PROP_IP_PORT, 11311);
			properties.put(ITerminalsConnectorConstants.PROP_TIMEOUT, 5);
			properties.put(ITerminalsConnectorConstants.PROP_ENCODING, null);
			properties.put(ITerminalsConnectorConstants.PROP_TITLE, "Liferay Gogo Shell");

			delegate.execute(properties, (Done)null);
		}
	}

	protected ILiferayServer getLiferayServer() {
		return (ILiferayServer)selectedServer.loadAdapter(ILiferayServer.class, null);
	}

	@Override
	protected int getRequiredServerState() {
		return IServer.STATE_STARTED;
	}

}