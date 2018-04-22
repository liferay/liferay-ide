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
public class SetExternalPropertiesCommand extends ServerCommand {

	public SetExternalPropertiesCommand(LiferayTomcatServer server, String externalProperties) {
		super(server, Msgs.setExternalProperties);
		this.externalProperties = externalProperties;
	}

	public void execute() {
		oldExternalProperties = ((LiferayTomcatServer)server).getExternalProperties();
		((LiferayTomcatServer)server).setExternalProperties(externalProperties);
	}

	public void undo() {
		((LiferayTomcatServer)server).setExternalProperties(oldExternalProperties);
	}

	protected String externalProperties;
	protected String oldExternalProperties;

	private static class Msgs extends NLS {

		public static String setExternalProperties;

		static {
			initializeMessages(SetExternalPropertiesCommand.class.getName(), Msgs.class);
		}

	}

}