/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.aws.ui.cmd;

import com.liferay.ide.eclipse.server.aws.core.BeanstalkServer;

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

@SuppressWarnings("restriction")
public class SetPasswordCommand extends ServerCommand {

	protected String oldPassword;
	protected String password;
	protected BeanstalkServer websphereServer;

	public SetPasswordCommand(BeanstalkServer server, String password) {
		super(null, "Set Password");
		this.websphereServer = server;
		this.password = password;
	}

	/**
	 * Execute setting the memory args
	 */
	public void execute() {
		oldPassword = websphereServer.getPassword();
		websphereServer.setPassword(password);
	}

	/**
	 * Restore prior memoryargs
	 */
	public void undo() {
		websphereServer.setPassword(oldPassword);
	}
}