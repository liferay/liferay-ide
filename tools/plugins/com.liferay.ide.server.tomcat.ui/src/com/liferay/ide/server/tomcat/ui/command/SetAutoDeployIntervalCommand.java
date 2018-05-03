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

import org.eclipse.jst.server.tomcat.core.internal.command.ServerCommand;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SetAutoDeployIntervalCommand extends ServerCommand {

	public SetAutoDeployIntervalCommand(LiferayTomcatServer server, String autoDeployInterval) {
		super(server, Msgs.setAutoDeployInterval);

		this.autoDeployInterval = autoDeployInterval;
	}

	public void execute() {
		oldAutoDeployInterval = ((LiferayTomcatServer)server).getAutoDeployInterval();
		((LiferayTomcatServer)server).setAutoDeployInterval(autoDeployInterval);
	}

	public void undo() {
		((LiferayTomcatServer)server).setAutoDeployInterval(oldAutoDeployInterval);
	}

	protected String autoDeployInterval;
	protected String oldAutoDeployInterval;

	private static class Msgs extends NLS {

		public static String setAutoDeployInterval;

		static {
			initializeMessages(SetAutoDeployIntervalCommand.class.getName(), Msgs.class);
		}

	}

}