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
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SetMemoryArgsCommand extends ServerCommand {

	public SetMemoryArgsCommand(LiferayTomcatServer server, String memoryArgs) {
		super(server, Messages.serverEditorActionSetDeployDirectory);

		this.memoryArgs = memoryArgs;
	}

	public void execute() {
		LiferayTomcatServer liferayServer = (LiferayTomcatServer)server;

		oldMemoryArgs = liferayServer.getMemoryArgs();
		liferayServer.setMemoryArgs(memoryArgs);
	}

	public void undo() {
		LiferayTomcatServer liferayServer = (LiferayTomcatServer)server;

		liferayServer.setMemoryArgs(oldMemoryArgs);
	}

	protected String memoryArgs;
	protected String oldMemoryArgs;

}