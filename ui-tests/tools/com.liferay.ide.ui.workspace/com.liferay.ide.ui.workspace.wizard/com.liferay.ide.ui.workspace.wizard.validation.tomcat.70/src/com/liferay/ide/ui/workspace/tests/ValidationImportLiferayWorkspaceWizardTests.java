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
import com.liferay.ide.ui.liferay.support.project.ImportLiferayWorkspaceProjectSupport;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Lily Li
 */
public class ValidationImportLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void checkExistingWorkspace() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.finish();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		validationAction.assertEquals(A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkInitialState() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		validationAction.assertEquals(PLEASE_SELECT_THE_WORKSPACE_LOCATION, wizardAction.getValidationMsg(2));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.importLiferayWorkspace.workspaceLocation());
		validationAction.assertEnabledTrue(wizardAction.importLiferayWorkspace.browseLocationBtn());
		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.importLiferayWorkspace.buildType());
		validationAction.assertCheckedFalse(wizardAction.importLiferayWorkspace.addProjectToWorkingSet());

		validationAction.assertEnabledTrue(wizardAction.getBackBtn());
		validationAction.assertEnabledFalse(wizardAction.getNextBtn());
		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());
		validationAction.assertEnabledTrue(wizardAction.getCancelBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkWithBundleInfoInitialState() {
		project.prepareServer();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		validationAction.assertActiveFalse(wizardAction.importLiferayWorkspace.serverName());
		validationAction.assertActiveTrue(wizardAction.importLiferayWorkspace.workspaceLocation());

		validationAction.assertEquals(
			SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, wizardAction.getValidationMsg(3));
		validationAction.assertTextEquals("gradle-liferay-workspace", wizardAction.importLiferayWorkspace.buildType());

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkWithoutBundleInfoInitialState() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		validationAction.assertEquals(
			SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, wizardAction.getValidationMsg(2));

		validationAction.assertTextEquals("gradle-liferay-workspace", wizardAction.importLiferayWorkspace.buildType());

		validationAction.assertCheckedFalse(wizardAction.importLiferayWorkspace.downloadLiferayBundle());

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.importLiferayWorkspace.selectDownloadLiferayBundle();

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.importLiferayWorkspace.deselectDownloadLiferayBundle();

		wizardAction.cancel();
	}

	@Test
	public void checkWorkspaceLocation() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "import-liferay-workspace-wizard-location.csv"))) {

			String env = msg.getOs();

			if (!env.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.importLiferayWorkspace.prepareLocation(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Rule
	public ImportLiferayWorkspaceProjectSupport project = new ImportLiferayWorkspaceProjectSupport(
		bot, "test-liferay-workspace-gradle");

}