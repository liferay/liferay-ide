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
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ValidationFragmentFilesTests extends SwtbotBase {

	@BeforeClass
	public static void init() throws IOException {
		envAction.unzipServer();
	}

	@Test
	public void checkInitialState() {
		wizardAction.openFileMenuFragmentFilesWizard();

		Assert.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, wizardAction.newFragmentFiles.projectName().getText());

		Assert.assertEquals("<None>", wizardAction.newFragmentFiles.runtimeName().getText());

		Assert.assertTrue(wizardAction.newFragmentFiles.newRuntimeBtn().isEnabled());

		Assert.assertEquals(StringPool.BLANK, wizardAction.newFragmentFiles.hostOsgiBundle().getText());

		Assert.assertFalse(wizardAction.newFragmentFiles.addOverrideFilesBtn().isEnabled());

		Assert.assertFalse(wizardAction.newFragmentFiles.deleteBtn().isEnabled());

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void testAddAllFilesOnFragment() {
		String projectName = "test-add-all-files-on-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.navigation.web");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void testAddFilesOnAllFilesFragment() {
		String projectName = "test-add-files-on-all-files-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.navigation.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		// wait for IDE-3566 fixed
		// Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void testAddFilesOnNoneFilesFragment() {
		String projectName = "test-add-files-on-none-files-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.newFragment.openNewRuntimeWizard();

		wizardAction.next();

		wizardAction.newRuntime7.prepare(envAction.getServerDir().toOSString());

		wizardAction.finish();

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_7_X);

		dialogAction.preferences.confirm();
	}

	@Test
	public void testAddFilesOnNonFragment() {
		String projectName = "test-add-files-on-non-fragment";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openFileMenuFragmentFilesWizard();

		Assert.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		Assert.assertFalse(wizardAction.newFragmentFiles.addOverrideFilesBtn().isEnabled());

		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

}