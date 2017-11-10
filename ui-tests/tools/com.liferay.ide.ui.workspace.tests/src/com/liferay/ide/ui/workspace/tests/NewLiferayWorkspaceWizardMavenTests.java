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
 */
public class NewLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	@Test
	public void createLiferayWorksapce() {
		String workspaceName = "maven-liferay-workspace";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(workspaceName);

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
	public void createLiferayWorksapceWithGradleMvcPortlet() {
		String workspaceName = "maven-liferay-workspace-with-gradle-mvc-portlet";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-gradle-mvc-portlet";

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorksapceWithTheme() {
		String workspaceName = "maven-liferay-workspace-with-theme";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-maven-theme-in-lr-ws";

		wizardAction.prepareLiferayModuleMaven(projectName, THEME);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName, workspaceName + "-wars (in wars)", projectName);

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorkspaceWithMvcPortlet() {
		String workspaceName = "maven-liferay-workspace-with-mvc-portlet";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-maven-mvc-portlet-in-lr-ws";

		wizardAction.prepareLiferayModuleMaven(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		projectName = "test-gradle-mvc-portlet";

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);

		viewAction.deleteProject(workspaceName);
	}

}