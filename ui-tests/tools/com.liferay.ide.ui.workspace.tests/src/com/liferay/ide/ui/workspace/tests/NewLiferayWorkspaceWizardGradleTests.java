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

import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Terry Jia
 */
public class NewLiferayWorkspaceWizardGradleTests extends SwtbotBase {

	@Test
	public void createLiferayWorksapce() {
		String workspaceName = "test-gradle-liferay-workspace";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorksapceChangeLocation() {
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundle() {
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundleChangeBundleUrl() {
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundleChangeServerName() {
	}

	@Test
	public void createLiferayWorksapceWithMavenMvcPortlet() {
		String workspaceName = "test-gradle-liferay-workspace";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven("test-maven-mvc-portlet", MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject("test-maven-mvc-portlet");

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorksapceWithMvcPortlet() {
		String workspaceName = "test-gradle-liferay-workspace";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-mvn-portlet-in-ws", MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorksapceWithTheme() {
		String workspaceName = "test-gradle-liferay-workspace";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-theme-in-ws", THEME);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorkspaceChangeModulesDir() {
		String workspaceName = "test-gradle-liferay-workspace-without-bundle";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		viewAction.openProjectFile(workspaceName, "gradle.properties");

		StringBuffer sb = new StringBuffer();

		sb.append("liferay.workspace.modules.dir=modulesTest");

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-mvc-portlet", MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorkspaceChangeWarsDir() {
		String workspaceName = "test-gradle-liferay-workspace-without-bundle";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		viewAction.openProjectFile(workspaceName, "gradle.properties");

		StringBuffer sb = new StringBuffer();

		sb.append("liferay.workspace.wars.dir=warsTest");

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-theme", THEME);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName);
	}

}