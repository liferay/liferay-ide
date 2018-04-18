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

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;

import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 */
public class SetServerManagerContextPathCommand extends RemoteServerCommand {

	public SetServerManagerContextPathCommand(IRemoteServerWorkingCopy server, String serverManagerContextPath) {
		super(server, Msgs.setServerManagerContextPath);

		this.serverManagerContextPath = serverManagerContextPath;
	}

	public void execute() {
		oldServerManagerContextPath = server.getServerManagerContextPath();

		server.setServerManagerContextPath(serverManagerContextPath);
	}

	public void undo() {
		server.setServerManagerContextPath(oldServerManagerContextPath);
	}

	protected String oldServerManagerContextPath;
	protected String serverManagerContextPath;

	private static class Msgs extends NLS {

		public static String setServerManagerContextPath;

		static {
			initializeMessages(SetServerManagerContextPathCommand.class.getName(), Msgs.class);
		}

	}

}