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

package com.liferay.ide.swtbot.workspace.ui.tests;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.ui.page.Editor;

import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class GradleLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void newGradleLiferayWorksapceProjectWizard() {
		String workspaceName = "test-gradle-liferay-workspace";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-mvn-portlet-in-ws", MVC_PORTLET);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-theme-in-ws", THEME);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven("test-maven-mvc-portlet", MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject("test-maven-mvc-portlet");

		viewAction.deleteProject(workspaceName);
	}

	@Test
	public void newGradleLiferayWorkspaceProjectWithoutDownloadBundle() {
		String workspaceName = "test-gradle-liferay-workspace-without-bundle";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(workspaceName);

		wizardAction.finishToWait();

		viewAction.fetchProjectFile(workspaceName, "gradle.properties").doubleClick();

		Editor gradlePropertiesEditor = ide.getEditor("gradle.properties");

		StringBuffer sb = new StringBuffer();

		sb.append("liferay.workspace.home.dir=bundlesTest");
		sb.append("\r");
		sb.append("liferay.workspace.modules.dir=modulesTest");
		sb.append("\r");
		sb.append("liferay.workspace.wars.dir=warsTest");

		gradlePropertiesEditor.setText(sb.toString());

		gradlePropertiesEditor.save();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-mvc-portlet", MVC_PORTLET);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle("test-theme", THEME);

		wizardAction.finishToWait();

		viewAction.deleteProject(workspaceName);
	}

}