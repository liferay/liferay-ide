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
import com.liferay.ide.ui.liferay.base.TimestampSupport;

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
		String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.finish();

		String[] moduleNames = {workspaceName, "modules"};

		Assert.assertTrue(viewAction.project.visibleFileTry(moduleNames));

		String[] themeNames = {workspaceName, "themes"};

		Assert.assertTrue(viewAction.project.visibleFileTry(themeNames));

		String[] warNames = {workspaceName, "wars"};

		Assert.assertTrue(viewAction.project.visibleFileTry(warNames));

		viewAction.project.closeAndDelete(workspaceName);
	}

	@Test
	public void createLiferayWorkspaceChangeLocation() {
		String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		String newFolderName = "changeLocation";

		wizardAction.newLiferayWorkspace.setLocation(workspacePath + "/" + newFolderName);

		wizardAction.finish();

		viewAction.project.closeAndDelete(workspaceName);
	}

	@Test
	public void createLiferayWorkspaceChangeModulesDir() {
		String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.finish();

		viewAction.project.openFile(workspaceName, "gradle.properties");

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

		String[] projectNames = {workspaceName, newModulesFolderName, projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);

		String[] newModuleNames = {workspaceName, newModulesFolderName};

		viewAction.project.closeAndDelete(newModuleNames);

		viewAction.project.closeAndDelete(workspaceName);
	}

	@Test
	public void createLiferayWorkspaceChangeWarsDir() {
		String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.finish();

		viewAction.project.openFile(workspaceName, "gradle.properties");

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

		String[] projectNames = {workspaceName, newWarsFolderName, projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);

		String[] newModuleNames = {workspaceName, newWarsFolderName};

		viewAction.project.closeAndDelete(newModuleNames);

		viewAction.project.closeAndDelete(workspaceName);
	}

	@Ignore("Ignore forever and test the download bundle in createLiferayWorkspaceWithDownloadBundleChangeBundleUrl")
	@Test
	public void createLiferayWorkspaceWithDownloadBundle() {
		String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.finish();

		viewAction.project.closeAndDelete(workspaceName);

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

		String workspaceName = timestamp.getName("test-liferay-workspace-gradle");

		// Use the internal server instead of the public server and also need to append the internal host

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.setServerName("Liferay 7-change-bundle-url");

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleUrl);

		wizardAction.finish();

		viewAction.project.closeAndDelete(workspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-change-bundle-url");

		dialogAction.preferences.confirm();
	}

	@Rule
	public TimestampSupport timestamp = new TimestampSupport(bot);

}