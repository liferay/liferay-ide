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
import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.page.Text;
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

		ComboBox buildTypeComboBox = wizardAction.newLiferayWorkspace.buildType();

		String[] buildTypes = buildTypeComboBox.items();

		validationAction.assertLengthEquals(expectedBuildTypes, buildTypes);

		for (int i = 0; i < buildTypes.length; i++) {
			validationAction.assertEquals(expectedBuildTypes[i], buildTypes[i]);
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

		validationAction.assertEquals(A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayWorkspaceWizard();

		validationAction.assertEquals(PLEASE_ENTER_THE_WORKSPACE_NAME, wizardAction.getValidationMsg(2));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newLiferayWorkspace.projectName());

		validationAction.assertCheckedTrue(wizardAction.newLiferayWorkspace.useDefaultLocation());

		validationAction.assertCheckedFalse(wizardAction.newLiferayWorkspace.downloadLiferayBundle());

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		String exceptLocation = envAction.getEclipseWorkspacePathOSString();

		Text location = wizardAction.newLiferayWorkspace.location();

		String actualLocation = location.getText();

		if ("win32".equals(Platform.getOS())) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(exceptLocation, actualLocation);

		wizardAction.newLiferayWorkspace.selectUseDefaultLocation();

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.deselectDownloadLiferayBundle();

		wizardAction.cancel();
	}

	@Test
	public void checkLiferayVersion() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String[] expectedLiferayVersions = {"7.0", "7.1"};

		ComboBox liferayVersionComboBox = wizardAction.newLiferayWorkspace.liferayVersion();

		String[] liferayVersions = liferayVersionComboBox.items();

		validationAction.assertLengthEquals(expectedLiferayVersions, liferayVersions);

		for (int i = 0; i < liferayVersions.length; i++) {
			validationAction.assertEquals(expectedLiferayVersions[i], liferayVersions[i]);
		}

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

			String os = msg.getOs();

			if (!os.equals(Platform.getOS())) {
				continue;
			}

			Text location = wizardAction.newLiferayWorkspace.location();

			location.setText(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkProjectName() {
		wizardAction.openNewLiferayWorkspaceWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-workspace-wizard-project-name.csv"))) {

			String os = msg.getOs();

			if (!os.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newLiferayWorkspace.prepareProjectName(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkServerName() {
	}

	@Test
	public void createLiferayWorkspaceWithInvalidBundleUrl() {
		String invalidMessage = "The bundle URL may not be a vaild URL.";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		String bundleHttpsErrorUrl = "https://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleHttpsErrorUrl);

		validationAction.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		String bundleHttpErrorUrl = "http://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleHttpErrorUrl);

		validationAction.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		String bundleFtpErrorUrl = "ftp://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleFtpErrorUrl);

		validationAction.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		wizardAction.cancel();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}