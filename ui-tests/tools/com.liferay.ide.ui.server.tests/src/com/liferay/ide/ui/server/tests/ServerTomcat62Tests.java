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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcat62Tests extends SwtbotBase {

	@BeforeClass
	public static void prepareServer() throws IOException {
		envAction.unzipServer62();

		envAction.preparePortalExtFile62();

		String serverName = "Liferay 62-init";

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare62();

		wizardAction.next();

		IPath serverDir = envAction.getServerDir62();

		IPath fullServerDir = serverDir.append(envAction.getServerName62());

		wizardAction.newRuntime62.prepare(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare62(serverName);

		wizardAction.finish();

		// String serverStoppedLabel = serverName + " [Stopped]";

		//

		// viewAction.serverStart(serverStoppedLabel);

		//

		// jobAction.waitForServerStarted(serverName);

		//

		// String serverStartedLabel = serverName + " [Started, Synchronized]";

		//

		// viewAction.openLiferayPortalHome(serverStartedLabel);

		//

		// viewAction.serverStop(serverStartedLabel);

		//

		// jobAction.waitForServerStopped(serverName);

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(serverName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void addLiferay62RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare62();

		wizardAction.next();

		String runtimeName = "Liferay 6.2-add-runtime";

		wizardAction.newRuntime62.prepare(runtimeName, envAction.getServerFullDir62().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void addLiferay62ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare62();

		wizardAction.next();

		String runtimeName = "Liferay 62-add-server";

		wizardAction.newRuntime62.prepare(runtimeName, envAction.getServerFullDir62().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare62("Liferay 62-add-server");

		wizardAction.finish();

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.preferences.confirm();
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

}