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

package com.liferay.ide.ui.fragment.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ValidationFragmentTests extends SwtbotBase {

	@Test
	public void checkBuildType() {
		wizardAction.openNewFragmentWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};
		String[] buildTypes = wizardAction.newFragment.buildType().items();

		int expectedLength = expectedBuildTypes.length;
		int length = buildTypes.length;

		Assert.assertEquals(expectedLength, length);

		for (int i = 0; i < buildTypes.length; i++) {
			Assert.assertTrue(buildTypes[i].equals(expectedBuildTypes[i]));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkHostOsgiBundle() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("gg");

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.prepareText("*blogs");

		dialogAction.selectTableItem("com.liferay.blogs.api-3.0.1.jar");

		dialogAction.selectTableItem("com.liferay.blogs.web-1.1.18.jar");

		dialogAction.selectTableItem("com.liferay.microblogs.web-2.0.15.jar");

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void checkInfoInitialState() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.next();

		Assert.assertEquals(StringPool.BLANK, wizardAction.newFragmentInfo.hostOsgiBundle().getText());

		Assert.assertTrue(wizardAction.newFragmentInfo.browseOsgiBtn().isEnabled());

		Assert.assertEquals(HOST_OSGI_BUNDLE_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(1));

		Assert.assertFalse(wizardAction.newFragmentInfo.addOverrideFilesBtn().isEnabled());

		Assert.assertFalse(wizardAction.newFragmentInfo.deleteBtn().isEnabled());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewFragmentWizard();

		Assert.assertEquals(StringPool.BLANK, wizardAction.newFragment.projectName().getText());

		Assert.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		Assert.assertTrue(wizardAction.newFragmentInfo.useDefaultLocation().isChecked());

		wizardAction.newProject.deselectUseDefaultLocation();

		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		if (Platform.getOS().equals("win32")) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(workspacePath, wizardAction.newFragmentInfo.location().getText());

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkLiferayRuntime() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertEquals(LIFERAY_RUNTIME_MUST_BE_CONFIGURED, wizardAction.getValidationMsg(2));

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		Assert.assertTrue(wizardAction.getNextBtn().isEnabled());

		Assert.assertEquals(
			CREATE_A_NEW_PROJECT_CONFIGURED_AS_A_LIFERAY_MODULE_PROJECT_FRAGMENT, wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void checkLocation() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.newFragment.deselectUseDefaultLocation();

		String workspacePath = envAction.getEclipseWorkspacePath().toOSString();

		if (Platform.getOS().equals("win32")) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(workspacePath, wizardAction.newFragmentInfo.location().getText());

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-fragment-wizard-project-location.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newFragment.location().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.newFragment.prepareGradle(projectName, workspacePath + "/testLocation");

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(projectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void checkOverridenFiles() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.next();

		String[] files = {
			"META-INF/resources/blogs_admin/configuration.jsp", "META-INF/resources/blogs_admin/entry_action.jsp",
			"META-INF/resources/blogs_admin/entry_search_columns.jspf", "resource-actions/default.xml",
			"portlet.properties"
		};

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentInfo.selectFile("META-INF/resources/blogs_admin/configuration.jsp");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.cancel();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void checkProjectName() {
		wizardAction.openNewFragmentWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-fragment-wizard-project-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newFragment.projectName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void createFragmentWithoutRuntime() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertEquals(LIFERAY_RUNTIME_MUST_BE_CONFIGURED, wizardAction.getValidationMsg(2));

		wizardAction.cancel();
	}

	@Test
	public void createFragmentWithoutRuntimeLiferayWorkspace() {
	}

}