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

import com.liferay.ide.server.core.ILiferayServerWorkingCopy;
import com.liferay.ide.server.core.LiferayServerCommand;

import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class SetUsernameCommand extends LiferayServerCommand {

	public SetUsernameCommand(ILiferayServerWorkingCopy server, String username) {
		super(server, Msgs.setUsername);

		this.username = username;
	}

	public void execute() {
		oldUsername = server.getUsername();

		server.setUsername(username);
	}

	public void undo() {
		server.setUsername(oldUsername);
	}

	protected String oldUsername;
	protected String username;

	private static class Msgs extends NLS {

		public static String setUsername;

		static {
			initializeMessages(SetUsernameCommand.class.getName(), Msgs.class);
		}

	}

}