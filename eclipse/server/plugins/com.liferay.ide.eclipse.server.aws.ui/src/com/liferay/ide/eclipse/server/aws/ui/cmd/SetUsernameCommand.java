
package com.liferay.ide.eclipse.server.aws.ui.cmd;

import com.liferay.ide.eclipse.server.aws.core.BeanstalkServer;

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;

@SuppressWarnings("restriction")
public class SetUsernameCommand extends ServerCommand {

	protected String oldUsername;
	protected String username;
	protected BeanstalkServer websphereServer;

	public SetUsernameCommand(BeanstalkServer server, String username) {
		super(null, "Set Username");
		this.websphereServer = server;
		this.username = username;
	}

	/**
	 * Execute setting the memory args
	 */
	public void execute() {
		oldUsername = websphereServer.getUsername();
		websphereServer.setUsername(username);
	}

	/**
	 * Restore prior memoryargs
	 */
	public void undo() {
		websphereServer.setUsername(oldUsername);
	}
}