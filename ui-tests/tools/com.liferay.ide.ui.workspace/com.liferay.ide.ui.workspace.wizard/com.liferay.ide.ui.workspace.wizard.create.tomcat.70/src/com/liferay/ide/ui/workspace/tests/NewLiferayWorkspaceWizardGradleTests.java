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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Terry Jia
 * @author Lily Li
 */
public class NewLiferayWorkspaceWizardGradleTests extends SwtbotBase {

	@Test
	public void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] moduleNames = {project.getName(), "modules"};

		Assert.assertTrue(viewAction.project.visibleFileTry(moduleNames));

		String[] themeNames = {project.getName(), "themes"};

		Assert.assertTrue(viewAction.project.visibleFileTry(themeNames));

		String[] warNames = {project.getName(), "wars"};

		Assert.assertTrue(viewAction.project.visibleFileTry(warNames));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspace71() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "7.1");

		wizardAction.newLiferayWorkspace.deselectDownloadLiferayBundle();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspaceChangeLocation() {
		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		String newFolderName = "changeLocation";

		wizardAction.newLiferayWorkspace.location().setText(workspacePath + "/" + newFolderName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspaceChangeModulesDir() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.openFile(project.getName(), "gradle.properties");

		StringBuffer sb = new StringBuffer();

		String newModulesFolderName = "modulesTest";

		sb.append("liferay.workspace.modules.dir");
		sb.append("=");
		sb.append(newModulesFolderName);

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-mvc-portlet";

		wizardAction.newModule.prepareGradle(projectName, MVC_PORTLET);

		wizardAction.finish();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {project.getName(), newModulesFolderName, projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);

		String[] newModuleNames = {project.getName(), newModulesFolderName};

		viewAction.project.closeAndDelete(newModuleNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLiferayWorkspaceChangeWarsDir() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.openFile(project.getName(), "gradle.properties");

		StringBuffer sb = new StringBuffer();

		String newWarsFolderName = "warsTest";

		sb.append("liferay.workspace.wars.dir");
		sb.append("=");
		sb.append(newWarsFolderName);

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-theme";

		wizardAction.newModule.prepareGradle("test-theme", THEME);

		wizardAction.finish();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {project.getName(), newWarsFolderName, projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);

		String[] newModuleNames = {project.getName(), newWarsFolderName};

		viewAction.project.closeAndDelete(newModuleNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("Ignore forever and test the download bundle in createLiferayWorkspaceWithDownloadBundleChangeBundleUrl")
	@Test
	public void createLiferayWorkspaceWithDownloadBundle() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.finish();

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

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.setServerName("Liferay 7-change-bundle-url");

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleUrl);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-change-bundle-url");

		dialogAction.preferences.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}