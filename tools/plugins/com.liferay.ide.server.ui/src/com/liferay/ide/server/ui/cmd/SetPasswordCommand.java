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
 * @author Greg Amerson
 */
public class SetPasswordCommand extends LiferayServerCommand {

	public SetPasswordCommand(ILiferayServerWorkingCopy server, String password) {
		super(server, Msgs.setPassword);

		this.password = password;
	}

	public void execute() {
		oldPassword = server.getPassword();

		server.setPassword(password);
	}

	public void undo() {
		server.setPassword(oldPassword);
	}

	protected String oldPassword;
	protected String password;

	private static class Msgs extends NLS {

		public static String setPassword;

		static {
			initializeMessages(SetPasswordCommand.class.getName(), Msgs.class);
		}

	}

}