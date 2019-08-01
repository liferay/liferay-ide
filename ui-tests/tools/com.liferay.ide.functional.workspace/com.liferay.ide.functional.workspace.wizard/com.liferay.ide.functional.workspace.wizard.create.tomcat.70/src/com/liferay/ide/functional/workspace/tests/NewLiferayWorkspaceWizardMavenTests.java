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

package com.liferay.ide.functional.workspace.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.project.ProjectsSupport;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Lily Li
 * @author Rui Wang
 */
public class NewLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	@Test
	public void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

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

		jobAction.waitForNoRunningJobs();

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
		String workspacePath = envAction.getEclipseWorkspacePathOSString();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName());

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		String newFolderName = "changeLocation";

		wizardAction.newLiferayWorkspace.prepareLocation(workspacePath + "/" + newFolderName);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

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

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1), PORTLET_PROVIDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-modules (in modules)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertContains("<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.delete(
			projects.getName(0), projects.getName(0) + "-modules (in modules)", projects.getName(1));

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-modules (in modules)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertContains("<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-modules (in modules)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-themes (in themes)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-wars (in wars)");

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void createLiferayWorkspaceCheckPomWithDeleteModuleFromDisk() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(projects.getName(0));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1), PORTLET_PROVIDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-modules (in modules)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertContains("<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.deleteProjectFromDisk(
			projects.getName(0), projects.getName(0) + "-modules (in modules)", projects.getName(1));

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-modules (in modules)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertDoesNotContains(
			"<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-modules (in modules)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-themes (in themes)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-wars (in wars)");

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void createLiferayWorkspaceCheckPomWithDeleteWar() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(projects.getName(0));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1), LAYOUT_TEMPLATE);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-wars (in wars)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertContains("<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.delete(projects.getName(0), projects.getName(0) + "-wars (in wars)", projects.getName(1));

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-wars (in wars)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertContains("<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-modules (in modules)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-themes (in themes)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-wars (in wars)");

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void createLiferayWorkspaceCheckPomWithDeleteWarFromDisk() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(projects.getName(0));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1), LAYOUT_TEMPLATE);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-wars (in wars)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertContains("<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		jobAction.waitForNoRunningJobs();

		viewAction.project.deleteProjectFromDisk(
			projects.getName(0), projects.getName(0) + "-wars (in wars)", projects.getName(1));

		viewAction.project.openFile(projects.getName(0), projects.getName(0) + "-wars (in wars)", "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		validationAction.assertDoesNotContains(
			"<module>" + projects.getName(1) + "</module>", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-modules (in modules)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-themes (in themes)");
		viewAction.project.closeAndDelete(projects.getName(0), projects.getName(0) + "-wars (in wars)");

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void createLiferayWorkspaceInitBundle() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = project.getName();

		wizardAction.newLiferayWorkspace.prepareMaven(projectName, "7.0");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.runMavenInitBundle(projectName);

		jobAction.waitForNoRunningJobs();

		jobAction.waitForConsoleContent(project.getName() + " [Maven Build]", "BUILD SUCCESS", M1);

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName, "bundles"));

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(0);

		dialogAction.preferences.confirm();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspaceSupportCustomizedFolder() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(project.getName(), "7.1");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "pom.xml");

		String text = "\t<liferayHome>test</liferayHome>\n";

		editorAction.customizedText(project.getName() + "/pom.xml", 15, 1, text);

		editorAction.save();

		editorAction.close();

		jobAction.waitForNoRunningJobs();

		viewAction.project.runMavenInitBundle(project.getName());

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "test", "tomcat-9.0.17"));

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(0);

		dialogAction.preferences.confirm();
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

		String serverName = "Liferay 7-change-bundle-url";

		wizardAction.newLiferayWorkspace.setServerName(serverName);

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleUrl);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		jobAction.waitForConsoleContent(project.getName() + " [Maven Build]", "BUILD SUCCESS", M1);

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "bundles"));

		Assert.assertTrue(viewAction.servers.visibleServer(serverName));

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(serverName);

		dialogAction.preferences.confirm();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "pom.xml");

		String text = "\t<liferayHome>test</liferayHome>\n";

		editorAction.customizedText(project.getName() + "/pom.xml", 15, 1, text);

		editorAction.save();

		editorAction.close();

		jobAction.waitForNoRunningJobs();

		viewAction.project.runMavenInitBundle(project.getName());

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "test"));

		Assert.assertTrue(viewAction.servers.visibleServer(LIFERAY_PORTAL_BUNDLE));

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_PORTAL_BUNDLE);

		dialogAction.preferences.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}