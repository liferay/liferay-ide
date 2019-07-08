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

package com.liferay.ide.functional.server.wizard.base;

import com.liferay.ide.ui.liferay.ServerTestBase;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcat7xBase extends ServerTestBase {

	public void addLiferay7RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		dialogAction.deleteRuntimFromPreferences(testServer.getServerName());

		resetTestServer();
	}

	public void addLiferay7ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(testServer.getServerName());

		wizardAction.finish();

		dialogAction.deleteRuntimFromPreferences(testServer.getServerName());

		resetTestServer();
	}

	public void serverEditorCustomLaunchSettingsChange() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(testServer.getServerName());

		wizardAction.finish();

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.selectCustomLaunchSettings();

		editorAction.server.selectUseDeveloperMode();

		editorAction.save();

		editorAction.close();

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.selectDefaultLaunchSettings();

		editorAction.save();

		editorAction.close();

		dialogAction.deleteRuntimFromPreferences(testServer.getServerName());

		resetTestServer();
	}

	public void serverEditorCustomLaunchSettingsChangeAndStart() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(testServer.getServerName());

		wizardAction.finish();

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.selectCustomLaunchSettings();

		editorAction.server.selectUseDeveloperMode();

		editorAction.save();

		editorAction.close();

		// viewAction.servers.start(serverStoppedLabel);

		// jobAction.waitForServerStarted(serverName);

		// String serverStartedLabel = serverName + "  [Started]";

		// viewAction.servers.stop(serverStartedLabel);

		// jobAction.waitForServerStopped(serverName);

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.selectDefaultLaunchSettings();

		editorAction.save();

		editorAction.close();

		dialogAction.deleteRuntimFromPreferences(testServer.getServerName());

		resetTestServer();
	}

	public void serverEditorPortsChange() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(testServer.getServerName());

		wizardAction.finish();

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.setHttpPort("8081");

		editorAction.save();

		editorAction.close();

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.setHttpPort("8080");

		editorAction.save();

		editorAction.close();

		resetTestServer();
	}

	public void serverEditorPortsChangeAndStart() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(testServer.getServerName());

		wizardAction.finish();

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.setHttpPort("8082");

		editorAction.save();

		editorAction.close();

		viewAction.servers.start(testServer.getStoppedLabel());

		jobAction.waitForServerStarted(testServer.getServerName());

		viewAction.servers.stop(testServer.getStartedLabel());

		jobAction.waitForServerStopped(testServer.getServerName());

		viewAction.servers.openEditor(testServer.getStoppedLabel());

		editorAction.server.setHttpPort("8080");

		editorAction.save();

		editorAction.close();

		resetTestServer();
	}

	public void testLiferay7ServerDebug() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(testServer.getServerName(), testServer.getFullServerDir());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(testServer.getServerName());

		wizardAction.finish();

		// String serverStoppedLabel = serverName + "  [Stopped]";

		// viewAction.servers.debug(serverStoppedLabel);

		// jobAction.waitForServerStarted(serverName);

		// String serverDebuggingLabel = serverName + "  [Debugging]";

		// viewAction.servers.stop(serverDebuggingLabel);

		// jobAction.waitForServerStopped(serverName);

		dialogAction.deleteRuntimFromPreferences(testServer.getServerName());

		resetTestServer();
	}

}