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

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class SetPortalServerPasswordCommand extends ServerCommand {

	public SetPortalServerPasswordCommand(IServerWorkingCopy server, String password) {
		super(server, Messages.editorResourceModifiedTitle);

		this.password = password;
	}

	public void execute() {
		PortalServer portalServer = (PortalServer)server.loadAdapter(PortalServer.class, null);

		oldPassword = portalServer.getUsername();

		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setPassword(password);
	}

	public void undo() {
		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setPassword(oldPassword);
	}

	protected String oldPassword;
	protected String password;

}