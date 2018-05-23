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

package com.liferay.ide.ui.liferay.support.server;

import com.liferay.ide.ui.liferay.support.SupportBase;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class ServerRunningSupport extends SupportBase {

	public ServerRunningSupport(SWTWorkbenchBot bot, ServerSupport server) {
		super(bot);

		_server = server;
	}

	@Override
	public void after() {
		viewAction.servers.stop(_server.getServerName());

		jobAction.waitForServerStopped(_server.getServerName());

		Assert.assertFalse("http://localhost:8080 still running", envAction.localConnected());

		super.after();
	}

	@Override
	public void before() {
		super.before();

		Assert.assertFalse("http://localhost:8080 still running", envAction.localConnected());

		viewAction.servers.start(_server.getStoppedLabel());

		jobAction.waitForServerStarted(_server.getServerName());

		Assert.assertTrue("Could not connent to http://localhost:8080", envAction.localConnected());
	}

	private ServerSupport _server;

}