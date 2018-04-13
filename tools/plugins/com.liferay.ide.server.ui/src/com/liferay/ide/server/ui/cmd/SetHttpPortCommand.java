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
public class SetHttpPortCommand extends RemoteServerCommand {

	public SetHttpPortCommand(IRemoteServerWorkingCopy server, String httpPort) {
		super(server, Msgs.setHttpPort);

		this.httpPort = httpPort;
	}

	public void execute() {
		oldHttpPort = server.getHTTPPort();

		server.setHTTPPort(httpPort);
	}

	public void undo() {
		server.setHTTPPort(oldHttpPort);
	}

	protected String httpPort;
	protected String oldHttpPort;

	private static class Msgs extends NLS {

		public static String setHttpPort;

		static {
			initializeMessages(SetHttpPortCommand.class.getName(), Msgs.class);
		}

	}

}