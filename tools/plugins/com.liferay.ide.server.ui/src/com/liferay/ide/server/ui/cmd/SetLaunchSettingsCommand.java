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
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class SetLaunchSettingsCommand extends ServerCommand {

	public SetLaunchSettingsCommand(IServerWorkingCopy server, boolean launchSettings) {
		super(server, Messages.editorResourceModifiedTitle);

		this.launchSettings = launchSettings;
	}

	public void execute() {
		PortalServer portalServer = (PortalServer)server.loadAdapter(PortalServer.class, null);

		oldLaunchSettings = portalServer.getLaunchSettings();

		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setLaunchSettings(launchSettings);
	}

	public void undo() {
		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setLaunchSettings(oldLaunchSettings);
	}

	protected boolean launchSettings;
	protected boolean oldLaunchSettings;

}