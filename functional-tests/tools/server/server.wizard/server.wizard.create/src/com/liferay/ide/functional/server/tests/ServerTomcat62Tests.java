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

package com.liferay.ide.functional.server.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.server.PureTomcat62Support;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcat62Tests extends SwtbotBase {

	@Test
	public void addLiferay62RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare62();

		wizardAction.next();

		wizardAction.newRuntime62.prepare(tomcat62.getServerName(), tomcat62.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		dialogAction.deleteRuntimFromPreferences(0);
	}

	@Test
	public void addLiferay62ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare62();

		wizardAction.next();

		wizardAction.newRuntime62.prepare(tomcat62.getServerName(), tomcat62.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare62(tomcat62.getServerName());

		wizardAction.finish();

		dialogAction.deleteRuntimFromPreferences(0);
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
	}

	@Test
	public void serverEditorCustomLaunchSettingsChangeAndStart() {
	}

	@Test
	public void serverEditorPortsChange() {
	}

	@Test
	public void serverEditorPortsChangeAndStart() {
	}

	@Test
	public void testLiferay62ServerDebug() {
	}

	@Rule
	public PureTomcat62Support tomcat62 = new PureTomcat62Support(bot);

}