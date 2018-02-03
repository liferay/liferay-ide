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
import com.liferay.ide.ui.liferay.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Lily Li
 */
public class ValidationLiferayWorkspaceTests extends SwtbotBase {

	@Test
	public void checkBuildType() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};
		String[] buildTypes = _newLiferayWorkspaceProjectWizard.getBuildTypes().items();

		int expectedLength = expectedBuildTypes.length;
		int length = buildTypes.length;

		Assert.assertEquals(expectedLength, length);

		for (int i = 0; i < buildTypes.length; i++) {
			Assert.assertTrue(buildTypes[i].equals(expectedBuildTypes[i]));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkBundleUrl() {
	}

	@Test
	public void checkExsitingLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = "test";

		wizardAction.newLiferayWorkspace.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(projectName);

		Assert.assertEquals(
			A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, _newLiferayWorkspaceProjectWizard.getValidationMsg());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_ENTER_THE_WORKSPACE_NAME, _newLiferayWorkspaceProjectWizard.getValidationMsg());
		Assert.assertEquals(StringPool.BLANK, _newLiferayWorkspaceProjectWizard.getProjectName().getText());

		Assert.assertTrue(_newLiferayWorkspaceProjectWizard.getUseDefaultLocation().isChecked());
		Assert.assertEquals(false, _newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().isChecked());

		_newLiferayWorkspaceProjectWizard.getUseDefaultLocation().deselect();

		String exceptLocation = envAction.getEclipseWorkspacePath().toOSString();
		String actualLocation = _newLiferayWorkspaceProjectWizard.getLocation().getText();

		if (Platform.getOS().equals("win32")) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(exceptLocation, actualLocation);

		_newLiferayWorkspaceProjectWizard.getUseDefaultLocation().select();

		_newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().select();
		_newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().deselect();

		wizardAction.cancel();
	}

	@Test
	public void checkLocation() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = "test";

		wizardAction.newLiferayWorkspace.prepareGradle(projectName);

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-workspace-wizard-location.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newLiferayWorkspace.setLocation(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkProjectName() {
		wizardAction.openNewLiferayWorkspaceWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-workspace-wizard-project-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newLiferayWorkspace.setProjectName(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkServerName() {
	}

	private NewLiferayWorkspaceWizard _newLiferayWorkspaceProjectWizard = new NewLiferayWorkspaceWizard(bot);

}