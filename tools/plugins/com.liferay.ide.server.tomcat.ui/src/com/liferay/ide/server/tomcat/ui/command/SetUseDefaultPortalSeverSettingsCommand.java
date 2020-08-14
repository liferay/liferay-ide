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

package com.liferay.ide.server.tomcat.ui.command;

import com.liferay.ide.server.tomcat.core.LiferayTomcatServer;

import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class SetUseDefaultPortalSeverSettingsCommand extends ServerCommand {

	public SetUseDefaultPortalSeverSettingsCommand(LiferayTomcatServer server, boolean useDefaultPortalServerSettings) {
		super(server, Messages.serverEditorActionSetDeployDirectory);

		this.useDefaultPortalServerSettings = useDefaultPortalServerSettings;
	}

	public void execute() {
		LiferayTomcatServer liferayServer = (LiferayTomcatServer)server;

		oldUseDefaultPortalServerSettings = liferayServer.getUseDefaultPortalServerSettings();
		liferayServer.setUseDefaultPortalServerSettings(useDefaultPortalServerSettings);
	}

	public void undo() {
		LiferayTomcatServer liferayServer = (LiferayTomcatServer)server;

		liferayServer.setUseDefaultPortalServerSettings(oldUseDefaultPortalServerSettings);
	}

	protected boolean oldUseDefaultPortalServerSettings;
	protected boolean useDefaultPortalServerSettings;

}