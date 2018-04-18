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

package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.remote.IRemoteServer;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class SetAdjustDeploymentTimestampCommand extends ServerCommand {

	public SetAdjustDeploymentTimestampCommand(IRemoteServer server, boolean adjustTimestamp) {
		super(null, Msgs.setDeployCustomPortletXML);

		remoteServer = server;
		adjustDemploymentTimestamp = adjustTimestamp;
	}

	public void execute() {
		oldAdjustDemploymentTimestamp = remoteServer.getAdjustDeploymentTimestamp();

		remoteServer.setAdjustDeploymentTimestamp(adjustDemploymentTimestamp);
	}

	public void undo() {
		remoteServer.setAdjustDeploymentTimestamp(oldAdjustDemploymentTimestamp);
	}

	protected boolean adjustDemploymentTimestamp;
	protected boolean oldAdjustDemploymentTimestamp;
	protected IRemoteServer remoteServer;

	private static class Msgs extends NLS {

		public static String setDeployCustomPortletXML;

		static {
			initializeMessages(SetAdjustDeploymentTimestampCommand.class.getName(), Msgs.class);
		}

	}

}