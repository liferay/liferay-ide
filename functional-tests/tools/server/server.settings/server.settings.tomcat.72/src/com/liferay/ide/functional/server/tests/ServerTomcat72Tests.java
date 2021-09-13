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

import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.server.PureTomcat72Support;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle72Support;
import com.liferay.ide.functional.server.wizard.base.ServerTomcat7xBase;

import java.io.File;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ServerTomcat72Tests extends ServerTomcat7xBase {

	@ClassRule
	public static LiferayWorkspaceGradle72Support liferayWorkspace = new LiferayWorkspaceGradle72Support(bot);

	@Test
	public void addCustomContext() {
		String folderDir = testServer.getFullServerDir() + "/" + testServer.getServerDir() + "/webapps";

		String xmlFileDir =
			testServer.getFullServerDir() + "/" + testServer.getServerDir() + "/conf/Catalina/localhost";

		File folder = new File(folderDir + "/ROOT");

		File xmlFile = new File(xmlFileDir + "/ROOT.xml");

		folder.renameTo(new File(folderDir + "/customName"));

		xmlFile.renameTo(new File(xmlFileDir + "/customName.xml"));

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

		viewAction.servers.start(testServer.getStoppedLabel());

		jobAction.waitForConsoleContent(testServer.getServerName() + " [Liferay 7.x]", "customName", 30 * 1000);

		jobAction.waitForServerStarted(testServer.getServerName());

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(testServer.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		Assert.assertTrue(viewAction.servers.visibleModuleTry(testServer.getStartedLabel(), project.getName()));

		jobAction.waitForConsoleContent(testServer.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(testServer.getServerName(), project.getName());

		dialogAction.confirm();

		jobAction.waitForConsoleContent(testServer.getServerName(), "STOPPED " + project.getName() + "_", M1);

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));

		viewAction.servers.stop(testServer.getStartedLabel());

		jobAction.waitForServerStopped(testServer.getServerName());

		dialogAction.deleteRuntimeFromPreferences(0);
	}

	@Override
	public ServerSupport getTestServer() {
		if ((testServer == null) || !(testServer instanceof PureTomcat72Support)) {
			testServer = new PureTomcat72Support(bot);
		}

		return testServer;
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ServerSupport tomcat = getTestServer();

}