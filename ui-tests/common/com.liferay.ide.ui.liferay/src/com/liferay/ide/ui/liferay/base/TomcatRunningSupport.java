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

package com.liferay.ide.ui.liferay.base;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class TomcatRunningSupport extends TomcatSupport {

	public TomcatRunningSupport(SWTWorkbenchBot bot) {
		super(bot);
	}

	@Override
	public void after() {
		viewAction.servers.stop(getServerStartedLabel());

		jobAction.waitForServerStopped(getServerName());

		Assert.assertFalse("http://localhost:8080 still running", envAction.localConnected());

		super.after();
	}

	@Override
	public void before() {
		super.before();

		Assert.assertFalse("http://localhost:8080 still running", envAction.localConnected());

		viewAction.servers.start(getServerStoppedLabel());

		jobAction.waitForServerStarted(getServerName());

		Assert.assertTrue("Could not connent to http://localhost:8080", envAction.localConnected());
	}

}