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
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SetCustomLaunchSettingsCommand extends ServerCommand {

	public SetCustomLaunchSettingsCommand(IServerWorkingCopy server, boolean customLaunchSettings) {
		super(server, Messages.editorResourceModifiedTitle);

		_customLaunchSettings = customLaunchSettings;
	}

	public void execute() {
		PortalServer portalServer = (PortalServer)server.loadAdapter(PortalServer.class, null);

		_oldLaunchSettings = portalServer.getCustomLaunchSettings();

		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setCustomLaunchSettings(_customLaunchSettings);
	}

	public void undo() {
		PortalServerDelegate portalServerDelegate = (PortalServerDelegate)server.loadAdapter(PortalServer.class, null);

		portalServerDelegate.setCustomLaunchSettings(_oldLaunchSettings);
	}

	private boolean _customLaunchSettings;
	private boolean _oldLaunchSettings;

}