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

import com.liferay.ide.functional.swtbot.page.Table;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class Tomcat62Support extends IdeServerSupport {

	public Tomcat62Support(SWTWorkbenchBot bot, ServerSupport server) {
		super(bot, server);
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

		wizardAction.newServerRuntime.prepare62();

		wizardAction.next();

		wizardAction.newRuntime62.prepare(server.getServerName(), server.getFullServerDir());

		wizardAction.finish();

		runtimes = dialogAction.serverRuntimeEnvironments.getRuntimes();

		Assert.assertTrue("Expect runtime number is 1 but now having " + runtimes.size(), runtimes.size() == 1);

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare62(server.getServerName());

		wizardAction.finish();
	}

}