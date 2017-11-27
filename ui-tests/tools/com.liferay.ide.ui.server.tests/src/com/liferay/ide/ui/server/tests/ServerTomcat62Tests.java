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

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay62RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir62();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName62());

		wizardAction.prepareLiferay62RuntimeInfo(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer62(serverName);

		wizardAction.finish();

		viewAction.showServersView();

		// String serverStoppedLabel = serverName + " [Stopped]";

		//

		// viewAction.serverStart(serverStoppedLabel);

		//

		// jobAction.waitForServer62Started(serverName);

		//

		// String serverStartedLabel = serverName + " [Started, Synchronized]";

		//

		// viewAction.openLiferayPortalHome(serverStartedLabel);

		//

		// viewAction.serverStop(serverStartedLabel);

		//

		// viewAction.serverStopWait62();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(serverName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferay62RuntimeFromPreferences() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay62RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 6.2-add-runtime";

		wizardAction.prepareLiferay62RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir62().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addLiferay62ServerFromMenu() {
		dialogAction.openPreferencesDialog();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay62RuntimeType();

		wizardAction.next();

		String runtimeName = "Liferay 62-add-server";

		wizardAction.prepareLiferay62RuntimeInfo(runtimeName, envAction.getLiferayServerFullDir62().toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer62("Liferay 62-add-server");

		wizardAction.finish();

		viewAction.showServersView();

		dialogAction.openPreferencesDialog();

		dialogAction.deleteRuntimeTryConfirm(runtimeName);

		dialogAction.confirmPreferences();
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