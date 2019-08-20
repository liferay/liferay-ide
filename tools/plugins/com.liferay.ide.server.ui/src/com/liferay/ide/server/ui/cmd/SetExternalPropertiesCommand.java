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
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SetExternalPropertiesCommand extends ServerCommand {

	public SetExternalPropertiesCommand(IServerWorkingCopy server, String externalProperties) {
		super(server, Messages.editorResourceModifiedTitle);

		this.externalProperties = externalProperties;
	}

	public void execute() {
		PortalServer portalServer = (PortalServer)server.loadAdapter(PortalServer.class, null);

		oldExternalProperties = portalServer.getExternalProperties();

		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setExternalProperties(externalProperties);
	}

	public void undo() {
		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setExternalProperties(oldExternalProperties);
	}

	protected String externalProperties;
	protected String oldExternalProperties;

}