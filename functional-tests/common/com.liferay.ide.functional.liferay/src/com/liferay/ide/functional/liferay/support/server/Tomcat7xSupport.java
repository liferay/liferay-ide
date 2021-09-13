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
import com.liferay.ide.functional.swtbot.page.Table;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class Tomcat7xSupport extends SupportBase {

	public Tomcat7xSupport(SWTWorkbenchBot bot, ServerSupport server) {
		super(bot);

		_server = server;
	}

	@Override
	public void after() {
		dialogAction.deleteRuntimeFromPreferences(0);

		super.after();
	}

	@Override
	public void before() {
		super.before();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		Table runtimes = dialogAction.serverRuntimeEnvironments.getRuntimes();

		for (int i = runtimes.size() - 1; i >= 0; i--) {
			dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(i);
		}

		runtimes = dialogAction.serverRuntimeEnvironments.getRuntimes();

		Assert.assertTrue("Expect there is no runtimes now having " + runtimes.size(), runtimes.size() == 0);

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(_server.getServerName(), _server.getFullServerDir());

		wizardAction.finish();

		runtimes = dialogAction.serverRuntimeEnvironments.getRuntimes();

		Assert.assertTrue("Expect runtime number is 1 but now having " + runtimes.size(), runtimes.size() == 1);

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(_server.getServerName());

		wizardAction.finish();
	}

	public ServerSupport getServer() {
		return _server;
	}

	private ServerSupport _server;

}