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

import com.liferay.ide.swtbot.liferay.ui.Actions;
import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.ui.page.CTabItem;
import com.liferay.ide.swtbot.ui.page.Editor;

import org.eclipse.swtbot.swt.finder.SWTBotAssert;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sunny Shi
 */
public class ImportLiferayWorkspaceProjectTests extends SwtbotBase {

	@Test
	public void importGradleLiferayWorkspaceProject() {
		String userHome = System.getProperty("user.dir");

		wizardAction.openImportLiferayWorkspaceWizard();

		wizardAction.prepareImportLiferayWorkspace(userHome + "/projects/testGradleWorkspace");

		String workspaceName = "testGradleWorkspace";

		wizardAction.finishToWait();

		viewAction.doActionOnProjectFile(Actions.getDelete(), workspaceName, "bundles");

		dialogAction.confirm();

		wizardAction.openImportLiferayWorkspaceWizard();

		wizardAction.prepareImportLiferayWorkspace(userHome + "/projects/testGradleWorkspace", true, "test-lrws");

		wizardAction.finishToWait();

		viewAction.doActionOnProjectFile(Actions.getDelete(), workspaceName, "bundles");

		dialogAction.confirm();

		viewAction.doActionOnProjectFile(Actions.getLiferayInitializeServerBundle(), workspaceName);

		sleep(10000);

		Assert.assertTrue(viewAction.fetchProjectFile(workspaceName, "bundles").isVisible());
		Assert.assertTrue(viewAction.fetchProjectFile(workspaceName, "configs").isVisible());
		Assert.assertTrue(viewAction.fetchProjectFile(workspaceName, "gradle").isVisible());

		String gradlePropertyFileName = "gradle.properties";
		String settingGradleFileName = "settings.gradle";

		viewAction.openProjectFile(workspaceName, gradlePropertyFileName);

		Editor gradlePropertiesFile = ide.getEditor(gradlePropertyFileName);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", gradlePropertiesFile.getText());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", gradlePropertiesFile.getText());

		gradlePropertiesFile.close();

		viewAction.openProjectFile(workspaceName, settingGradleFileName);

		Editor settingGradleFile = ide.getEditor(settingGradleFileName);

		SWTBotAssert.assertContains("buildscript", settingGradleFile.getText());
		SWTBotAssert.assertContains("repositories", settingGradleFile.getText());

		settingGradleFile.close();
	}

	@Test
	public void importMavenLiferayWorkspaceProject() {
		wizardAction.openImportLiferayWorkspaceWizard();

		String liferayWorkspaceName = "testMavenWorkspace";

		String userHome = System.getProperty("user.dir");

		wizardAction.prepareImportLiferayWorkspace(userHome + "/projects/testGradleWorkspace");

		wizardAction.finishToWait();

		viewAction.openProjectFile(liferayWorkspaceName, "testMavenWorkspace-modules", "pom.xml");

		CTabItem switchCTabItem = new CTabItem(bot, "pom.xml");
		sleep();
		switchCTabItem.click();

		Editor pomXmlFileModules = ide.getEditor("testMavenWorkspace-modules/pom.xml");

		SWTBotAssert.assertContains("testMavenWorkspace-modules", pomXmlFileModules.getText());
		SWTBotAssert.assertContains("artifactId", pomXmlFileModules.getText());

		pomXmlFileModules.close();

		viewAction.openProjectFile(liferayWorkspaceName, "pom.xml");

		switchCTabItem.click();

		Editor pomXmlFile = ide.getEditor("testMavenWorkspace/pom.xml");

		SWTBotAssert.assertContains("testMavenWorkspace", pomXmlFile.getText());

		pomXmlFile.close();
	}

}