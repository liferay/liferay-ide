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

package com.liferay.ide.swtbot.server.ui.tests;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.editor.ServerEditor;

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

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName());

		wizardAction.prepareLiferay7RuntimeInfo(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.serverStart(serverStoppedLabel);

		sleep(200000);

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.openLiferayPortalHome(serverStartedLabel);

		sleep(20000);

		viewAction.serverStop(serverStartedLabel);

		sleep(20000);
	}

	@Test
	public void addLiferay7RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(
			"Liferay 7-add-runtime", envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();
	}

	@Test
	public void addLiferay7ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(
			"Liferay 7-add-server", envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer("Liferay 7-add-server");

		wizardAction.finish();

		viewAction.showServersView();
	}

	@Test
	public void deleteLiferay7RuntimeFromPreferences() {
		String runtimeName = "Liferay 7-delete";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.deleteRuntime(runtimeName);

		dialogAction.confirm();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
		String serverName = "Liferay 7-custom-launch-settings";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

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

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabel.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabel.close();
		}

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getDefaultLaunchSettings().click();
		}
		catch (Exception e) {
			serverEditorWithLabel.getDefaultLaunchSettings().click();
		}

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabel.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabel.close();
		}
	}

	@Test
	public void serverEditorCustomLaunchSettingsChangeAndStart() {
		String serverName = "Liferay 7-custom-launch-settings-start";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

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

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.close();
		}

		viewAction.serverStart(serverStoppedLabel);

		sleep(100000);

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.serverStop(serverStartedLabel);

		sleep(20000);

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getDefaultLaunchSettings().click();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getDefaultLaunchSettings().click();
		}

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.close();
		}
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChange() {
		String serverName = "Liferay 7-http-port-change";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

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

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabel.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabel.close();
		}

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getHttpPort().setText("8080");
		}
		catch (Exception e) {
			serverEditorWithLabel.getHttpPort().setText("8080");
		}

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabel.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabel.close();
		}
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChangeAndStart() {
		String serverName = "Liferay 7-http-port-change-and-start";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

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

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.close();
		}

		viewAction.serverStart(serverStoppedLabel);

		sleep(100000);

		String serverStartedLabel = serverName + "  [Started]";

		viewAction.serverStop(serverStartedLabel);

		sleep(20000);

		viewAction.openServerEditor(serverStoppedLabel);

		try {
			serverEditor.getHttpPort().setText("8080");
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.getHttpPort().setText("8080");
		}

		try {
			serverEditor.save();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.save();
		}

		try {
			serverEditor.close();
		}
		catch (Exception e) {
			serverEditorWithLabelStopped.close();
		}
	}

	@Test
	public void testLiferay7ServerDebug() {
		String serverName = "Liferay 7-debug";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(serverName, envAction.getLiferayServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		String serverStoppedLabel = serverName + "  [Stopped]";

		viewAction.serverDebug(serverStoppedLabel);

		sleep(100000);

		String serverDebuggingLabel = serverName + "  [Debugging]";

		viewAction.serverStop(serverDebuggingLabel);

		sleep(20000);
	}

}