package com.liferay.ide.ui.liferay;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class TomcatBase extends SwtbotBase implements ServerSupport {

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

	public static String getServerName() {
		return "tomcat-support";
	}

	public static String getServerStartedLabel() {
		return getServerName() + STARTED_LABEL;
	}

	public static String getServerStoppedLabel() {
		return getServerName() + STOPPED_LABEL;
	}

}
