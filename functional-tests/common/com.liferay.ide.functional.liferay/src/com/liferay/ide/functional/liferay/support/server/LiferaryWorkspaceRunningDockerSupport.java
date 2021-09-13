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

package com.liferay.ide.functional.liferay.support.server;

import com.liferay.ide.functional.liferay.support.SupportBase;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Lily li
 */
public class LiferaryWorkspaceRunningDockerSupport extends SupportBase {

	public LiferaryWorkspaceRunningDockerSupport(SWTWorkbenchBot bot, LiferaryWorkspaceDockerSupport workspace) {
		super(bot);

		_workspace = workspace;
	}

	@Override
	public void after() {
		viewAction.servers.stop(_workspace.getDockerStartedLabel());

		jobAction.waitForServerStopped(_workspace.getDockerServerName());

		Assert.assertFalse("http://localhost:8080 still running", envAction.localConnected());

		super.after();
	}

	@Override
	public void before() {
		super.before();

		viewAction.servers.start(_workspace.getDockerStoppedLabel());

		jobAction.waitForConsoleContent(
			_workspace.getDockerServerName() + " [Liferay Docker]",
			"-Djava.protocol.handler.pkgs=org.apache.catalina.webresources", 30 * 1000);

		jobAction.waitForConsoleContent(
			_workspace.getDockerServerName() + " [Liferay Docker]",
			"-Dorg.apache.catalina.security.SecurityListener.UMASK", 30 * 1000);

		jobAction.waitForServerStarted(_workspace.getDockerServerName());

		viewAction.servers.openGogoShell(_workspace.getDockerStartedLabel());

		validationAction.assertGogoShellVisible();

		viewAction.closeView("Terminal");
	}

	private LiferaryWorkspaceDockerSupport _workspace;

}