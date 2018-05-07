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
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Rule;
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
		String[] buildTypes = wizardAction.newLiferayWorkspace.buildType().items();

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

		Assert.assertEquals(A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_ENTER_THE_WORKSPACE_NAME, wizardAction.getValidationMsg(2));
		Assert.assertEquals(StringPool.BLANK, wizardAction.newLiferayWorkspace.projectName().getText());

		Assert.assertTrue(wizardAction.newLiferayWorkspace.useDefaultLocation().isChecked());
		Assert.assertEquals(false, wizardAction.newLiferayWorkspace.downloadLiferayBundle().isChecked());

		wizardAction.newLiferayWorkspace.useDefaultLocation().deselect();

		String exceptLocation = envAction.getEclipseWorkspacePath().toOSString();
		String actualLocation = wizardAction.newLiferayWorkspace.location().getText();

		if (Platform.getOS().equals("win32")) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(exceptLocation, actualLocation);

		wizardAction.newLiferayWorkspace.useDefaultLocation().select();

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.downloadLiferayBundle().deselect();

		wizardAction.cancel();
	}

	@Test
	public void createLiferayWorkspaceWithInvalidBundleUrl() {
		String invalidMessage = "The bundle URL may not be a vaild URL.";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		String bundleHttpsErrorUrl = "https://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleHttpsErrorUrl);

		Assert.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		String bundleHttpErrorUrl = "http://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleHttpErrorUrl);

		Assert.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		String bundleFtpErrorUrl = "ftp://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleFtpErrorUrl);

		Assert.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

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

			wizardAction.newLiferayWorkspace.location().setText(msg.getInput());

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

			wizardAction.newLiferayWorkspace.projectName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkServerName() {
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}