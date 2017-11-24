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
import com.liferay.ide.ui.liferay.page.editor.ServerEditor;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcatTests extends SwtbotBase {

	@BeforeClass
	public static void prepareServer() throws IOException {
		envAction.unzipServer();

		envAction.prepareGeoFile();

		envAction.preparePortalExtFile();

		envAction.preparePortalSetupWizardFile();

		String serverName = "Liferay 7-initialization";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName());

		wizardAction.prepareLiferay7RuntimeInfo(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

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

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferay7RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 7-add-runtime";

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferay7ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 7-add-server";

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer("Liferay 7-add-server");

		wizardAction.finish();

		viewAction.showServersView();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
		String serverName = "Liferay 7-custom-launch-settings";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		ServerEditor serverEditor = new ServerEditor(bot, serverName);
		ServerEditor serverEditorWithLabel = new ServerEditor(bot, serverStoppedLabel);

		try {
			serverEditor.getCustomLaunchSettings().click();
		}
		catch (Exception e) {
			serverEditorWithLabel.getCustomLaunchSettings().click();
		}

		try {
			serverEditor.getUseDeveloperMode().select();
		}
		catch (Exception e) {
			serverEditorWithLabel.getUseDeveloperMode().select();
		}

		editorAction.save();

		editorAction.close();

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getDefaultLaunchSettings().click();
		}
		catch (Exception e) {
			serverEditorWithLabel.getDefaultLaunchSettings().click();
		}

		editorAction.save();

		editorAction.close();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Ignore("ignore to wait Terry finish the server start checker")
	@Test
	public void serverEditorCustomLaunchSettingsChangeAndStart() {
		String serverName = "Liferay 7-custom-launch-settings-start";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		ServerEditor serverEditor = new ServerEditor(bot, serverName);
		ServerEditor serverEditorWithLabelStopped = new ServerEditor(bot, serverStoppedLabel);

		try {
			serverEditor.getCustomLaunchSettings().click();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getCustomLaunchSettings().click();
		}

		try {
			serverEditor.getUseDeveloperMode().select();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getUseDeveloperMode().select();
		}

		editorAction.save();

		editorAction.close();

		viewAction.serverStart(serverStoppedLabel);

		viewAction.serverStartWait();

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.serverStop(serverStartedLabel);

		viewAction.serverStopWait();

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getDefaultLaunchSettings().click();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getDefaultLaunchSettings().click();
		}

		editorAction.save();

		editorAction.close();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChange() {
		String serverName = "Liferay 7-http-port-change";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		ServerEditor serverEditor = new ServerEditor(bot, serverName);
		ServerEditor serverEditorWithLabel = new ServerEditor(bot, serverStoppedLabel);

		try {
			serverEditor.getHttpPort().setText("8081");
		}
		catch (Exception e) {
			serverEditorWithLabel.getHttpPort().setText("8081");
		}

		editorAction.save();

		editorAction.close();

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getHttpPort().setText("8080");
		}
		catch (Exception e) {
			serverEditorWithLabel.getHttpPort().setText("8080");
		}

		editorAction.save();

		editorAction.close();
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChangeAndStart() {
		String serverName = "Liferay 7-http-port-change-and-start";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.openServerEditor(serverStoppedLabel);

		ServerEditor serverEditor = new ServerEditor(bot, serverName);
		ServerEditor serverEditorWithLabelStopped = new ServerEditor(bot, serverStoppedLabel);

		try {
			serverEditor.getHttpPort().setText("8082");
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getHttpPort().setText("8082");
		}

		editorAction.save();

		editorAction.close();

		viewAction.serverStart(serverStoppedLabel);

		viewAction.serverStartWait();

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.serverStop(serverStartedLabel);

		viewAction.serverStopWait();

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getHttpPort().setText("8080");
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getHttpPort().setText("8080");
		}

		editorAction.save();

		editorAction.close();
	}

	@Ignore("ignore to wait Terry finish the server start checker")
	@Test
	public void testLiferay7ServerDebug() {
		String serverName = "Liferay 7-debug";

		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.serverDebug(serverStoppedLabel);

		viewAction.serverStartWait();

		String serverDebuggingLabel = serverName + "  [Debugging]";

		viewAction.serverStop(serverDebuggingLabel);

		viewAction.serverStopWait();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

}