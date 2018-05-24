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

package com.liferay.ide.ui.workspace.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.project.ProjectsSupport;

import org.eclipse.swtbot.swt.finder.SWTBotAssert;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Lily Li
 */
public class NewLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	@Test
	public void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName());

		wizardAction.finish();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(moduleNames));

		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(themeNames));

		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(warNames));

		String[] pomfile = {project.getName(), "pom.xml"};

		Assert.assertTrue(viewAction.project.visibleFileTry(pomfile));

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspace71() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName(), "7.1");

		wizardAction.newLiferayWorkspace.deselectDownloadLiferayBundle();

		wizardAction.finish();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspaceChangeLocation() {
		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName());

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		String newFolderName = "changeLocation";

		wizardAction.newLiferayWorkspace.location().setText(workspacePath + "/" + newFolderName);

		wizardAction.finish();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspaceCheckPomWithDeleteModule() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(projects.getName(0));

		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1), PORTLET);

		wizardAction.finish();

		String[] moduleNames = {projects.getName(0), projects.getName(0) + "-modules (in modules)"};
		String[] themeNames = {projects.getName(0), projects.getName(0) + "-themes (in themes)"};
		String[] warNames = {projects.getName(0), projects.getName(0) + "-wars (in wars)"};

		String modulePomXmlText = "<module>" + projects.getName(1) + "</module>";

		viewAction.project.openFile(moduleNames[0], moduleNames[1], "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		SWTBotAssert.assertContains(modulePomXmlText, editorAction.getContent());

		editorAction.close();

		String[] projectNames = {moduleNames[0], moduleNames[1], projects.getName(1)};

		viewAction.project.closeAndDeleteFromDisk(projectNames);

		// wait for IDE-3987 fixed

		// viewAction.project.openFile(moduleNames[0], moduleNames[1], "pom.xml");

		// editorAction.pomXml.switchTabPomXml();

		// SWTBotAssert.assertDoesNotContain(modulePomXmlText, editorAction.getContent());

		// editorAction.close();

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void createLiferayWorkspaceCheckPomWithDeleteWar() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(projects.getName(0));

		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1), LAYOUT_TEMPLATE);

		wizardAction.finish();

		String[] moduleNames = {projects.getName(0), projects.getName(0) + "-modules (in modules)"};
		String[] themeNames = {projects.getName(0), projects.getName(0) + "-themes (in themes)"};
		String[] warNames = {projects.getName(0), projects.getName(0) + "-wars (in wars)"};

		String modulePomXmlText = "<module>" + projects.getName(1) + "</module>";

		viewAction.project.openFile(warNames[0], warNames[1], "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		SWTBotAssert.assertContains(modulePomXmlText, editorAction.getContent());

		editorAction.close();

		String[] projectNames = {warNames[0], warNames[1], projects.getName(1)};

		viewAction.project.closeAndDeleteFromDisk(projectNames);

		// wait for IDE-3987 fixed

		// viewAction.project.openFile(warNames[0], warNames[1], "pom.xml");

		// editorAction.pomXml.switchTabPomXml();

		// SWTBotAssert.assertDoesNotContain(modulePomXmlText, editorAction.getContent());

		// editorAction.close();

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Ignore
	@Test
	public void createLiferayWorkspaceInitBundle() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = project.getName();

		wizardAction.newLiferayWorkspace.prepareMaven(projectName);

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.setServerName(projectName);

		wizardAction.newLiferayWorkspace.setBundleUrl(
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip");

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName, "bundles"));

		viewAction.project.runMavenInitBundle(projectName);

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.servers.visibleServer(LIFERAY_PORTAL_BUNDLE));

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_PORTAL_BUNDLE);

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(projectName);

		dialogAction.preferences.confirm();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("Ignore forever and test the download bundle in createLiferayWorkspaceWithDownloadBundleChangeBundleUrl")
	@Test
	public void createLiferayWorkspaceWithDownloadBundle() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.finish();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-download-bundle");

		dialogAction.preferences.confirm();
	}

	@Ignore("waiting for server configuration")
	@Test
	public void createLiferayWorkspaceWithDownloadBundleChangeBundleUrl() {
		if (!envAction.internal()) {
			return;
		}

		// Use the internal server instead of the public server and also need to append the internal host

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.setServerName("Liferay 7-change-bundle-url");

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleUrl);

		wizardAction.finish();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());

		// wait for IDE-3595 fixed
		// dialogAction.openPreferencesDialog();

		// dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		// dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-change-bundle-url");

		// dialogAction.preferences.confirm();

	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}