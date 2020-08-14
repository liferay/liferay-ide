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
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class SetServerModeCommand extends ServerCommand {

	public SetServerModeCommand(LiferayTomcatServer server, int serverMode) {
		super(server, Messages.serverEditorActionSetDeployDirectory);

		this.serverMode = serverMode;
	}

	public void execute() {
		LiferayTomcatServer liferayServer = (LiferayTomcatServer)server;

		oldServerMode = liferayServer.getServerMode();
		liferayServer.setServerMode(serverMode);
	}

	public void undo() {
		LiferayTomcatServer liferayServer = (LiferayTomcatServer)server;

		liferayServer.setServerMode(oldServerMode);
	}

	protected int oldServerMode;
	protected int serverMode;

}