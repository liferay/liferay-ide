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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Terry Jia
 * @author Lily Li
 */
public class NewLiferayWorkspaceWizardGradleTests extends SwtbotBase {

	@Test
	public void createLiferayWorksapce() {
		String workspaceName = "test-liferay-workspace-gradle";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finish();

		viewAction.closeAndDeleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorksapceChangeLocation() {
		String workspaceName = "test-liferay-workspace-gradle-change-location";

		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.getUseDefaultLocation().deselect();

		String newFolderName = "changeLocation";

		wizardAction.getLocation().setText(workspacePath + "/" + newFolderName);

		wizardAction.finish();

		viewAction.closeAndDeleteProject(workspaceName);
	}

	@Ignore("ignore every tests which need download progress to wait internal resource server")
	@Test
	public void createLiferayWorksapceWithDownloadBundle() {
		String workspaceName = "test-liferay-workspace-gradle-download-bundle";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.getDownloadLiferayBundle().select();

		wizardAction.getServerName().setText("Liferay 7-download-bundle");

		wizardAction.finish();

		viewAction.closeAndDeleteProject(workspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.deleteRuntimeTryConfirm("Liferay 7-download-bundle");

		dialogAction.confirmPreferences();
	}

	@Ignore("ignore every tests which need download progress to wait internal resource server")
	@Test
	public void createLiferayWorksapceWithDownloadBundleChangeBundleUrl() {
		String workspaceName = "test-liferay-workspace-gradle-change-bundle-url";

		String bundleUrl =
			"http://mirrors.dlc.liferay.com/portal/7.0.3-ga4/liferay-ce-portal-tomcat-7.0-ga4-20170613175008905.zip";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.getDownloadLiferayBundle().select();

		wizardAction.getServerName().setText("Liferay 7-change-bundle-url");

		wizardAction.getBundleUrl().setText(bundleUrl);

		wizardAction.finish();

		viewAction.closeAndDeleteProject(workspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.deleteRuntimeTryConfirm("Liferay 7-change-bundle-url");

		dialogAction.confirmPreferences();
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundleChangeServerName() {
	}

	@Test
	public void createLiferayWorkspaceChangeModulesDir() {
		String workspaceName = "test-gradle-liferay-workspace-change-modules-dir";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finish();

		viewAction.openProjectFile(workspaceName, "gradle.properties");

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

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finish();

		String[] projectNames = {workspaceName, newModulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);

		viewAction.closeAndDeleteProject(workspaceName);

		viewAction.closeAndDeleteProject(newModulesFolderName);
	}

	@Test
	public void createLiferayWorkspaceChangeWarsDir() {
		String workspaceName = "test-gradle-liferay-workspace-change-wars-dir";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finish();

		viewAction.openProjectFile(workspaceName, "gradle.properties");

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

		wizardAction.prepareLiferayModuleGradle("test-theme", THEME);

		wizardAction.finish();

		String[] projectNames = {workspaceName, newWarsFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);

		viewAction.closeAndDeleteProject(workspaceName);

		viewAction.closeAndDeleteProject(newWarsFolderName);
	}

}