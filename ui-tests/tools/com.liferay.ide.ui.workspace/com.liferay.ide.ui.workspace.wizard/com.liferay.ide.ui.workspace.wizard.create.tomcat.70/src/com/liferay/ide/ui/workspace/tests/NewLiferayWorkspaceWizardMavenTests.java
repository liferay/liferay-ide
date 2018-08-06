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

		jobAction.waitForNoRunningProjectBuildingJobs();

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

		jobAction.waitForNoRunningProjectBuildingJobs();

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

		jobAction.waitForNoRunningProjectBuildingJobs();

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

		wizardAction.newModule.prepareMaven(projects.getName(1), PORTLET);

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

		wizardAction.newModule.prepareMaven(projects.getName(1), PORTLET);

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

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName, "bundles"));

		viewAction.project.runMavenInitBundle(projectName);

		jobAction.waitForNoRunningProjectBuildingJobs();

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

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] moduleNames = {project.getName(), project.getName() + "-modules (in modules)"};
		String[] themeNames = {project.getName(), project.getName() + "-themes (in themes)"};
		String[] warNames = {project.getName(), project.getName() + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-change-bundle-url");

		dialogAction.preferences.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}