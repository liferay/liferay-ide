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
 * @author Simon Jiang
 */
public class ServerWildflyTests extends SwtbotBase {

	@BeforeClass
	public static void prepareServer() throws IOException {
		envAction.unzipWildflyServer();

		envAction.prepareGeoFile();

		envAction.preparePortalWildflyExtFile();

		envAction.prepareWildflyPortalSetupWizardFile();

		String serverName = "Liferay Wildfly 7-initialization";

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		IPath serverDir = envAction.getWildflyServerDir();

		IPath fullServerDir = serverDir.append(envAction.getWildflyServerName());

		wizardAction.newRuntime7.prepare(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		// String serverStoppedLabel = serverName + " [Stopped]";

		//

		// viewAction.serverStart(serverStoppedLabel);

		//

		// viewAction.serverStartWait();

		//

		// String serverStartedLabel = serverName + " [Started]";

		//

		// viewAction.openLiferayPortalHome(serverStartedLabel);

		//

		// viewAction.serverStop(serverStartedLabel);

		//

		// viewAction.serverStopWait();

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(serverName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void addLiferay7RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		String runtimeName = "Liferay 7-wildfly-runtime";

		wizardAction.newRuntime7.prepare(runtimeName, envAction.getWildflyServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void addLiferayWildflyServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		String runtimeName = "Liferay 7-wildfly-server";

		wizardAction.newRuntime7.prepare(runtimeName, envAction.getWildflyServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare("Liferay 7-wildfly-server");

		wizardAction.finish();

		viewAction.showServersView();

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
		String serverName = "Liferay 7-wildfly-custom-launch-settings";

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(serverName, envAction.getWildflyServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.servers.openEditor(serverStoppedLabel);


		editorAction.server.selectCustomLaunchSettings();

		editorAction.server.selectUseDeveloperMode();

		editorAction.save();

		editorAction.close();

		viewAction.servers.openEditor(serverStoppedLabel);

		editorAction.server.selectDefaultLaunchSettings();

		editorAction.save();

		editorAction.close();

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(serverName);

		dialogAction.preferences.confirm();
	}

}