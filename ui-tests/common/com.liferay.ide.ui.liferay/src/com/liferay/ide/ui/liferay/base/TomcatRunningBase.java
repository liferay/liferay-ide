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

package com.liferay.ide.ui.liferay.base;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Terry Jia
 */
public abstract class TomcatRunningBase extends SwtbotBase implements ServerSupport {

	public static String getServerName() {
		return "tomcat-support";
	}

	public static String getServerStartedLabel() {
		return getServerName() + STARTED_LABEL;
	}

	public static String getServerStoppedLabel() {
		return getServerName() + STOPPED_LABEL;
	}

	@BeforeClass
	public static void startServer() throws IOException {
		envAction.unzipServer();

		envAction.prepareGeoFile();

		envAction.preparePortalExtFile();

		envAction.preparePortalSetupWizardFile();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(getServerName(), envAction.getServerFullDir().toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(getServerName());

		wizardAction.finish();

		viewAction.servers.start(getServerStoppedLabel());

		jobAction.waitForServerStarted(getServerName());
	}

	@AfterClass
	public static void stopServer() throws IOException {
		viewAction.servers.stop(getServerStartedLabel());

		jobAction.waitForServerStopped(getServerName());

		dialogAction.openPreferencesDialog();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(getServerName());

		dialogAction.preferences.confirm();
	}

}