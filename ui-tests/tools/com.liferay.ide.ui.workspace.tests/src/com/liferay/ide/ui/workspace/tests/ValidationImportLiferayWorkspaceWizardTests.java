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
import com.liferay.ide.ui.liferay.base.ImportLiferayWorkspaceProjectSupport;
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
public class ValidationImportLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void checkExistingWorkspace() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.finish();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		Assert.assertEquals(A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, wizardAction.getValidationMsg(2));
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkInitialState() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_SELECT_THE_WORKSPACE_LOCATION, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, wizardAction.importLiferayWorkspace.workspaceLocation().getText());
		Assert.assertTrue(wizardAction.importLiferayWorkspace.browseLocationBtn().isEnabled());
		Assert.assertEquals(StringPool.BLANK, wizardAction.importLiferayWorkspace.buildType().getText());
		Assert.assertFalse(wizardAction.importLiferayWorkspace.addProjectToWorkingSet().isChecked());

		Assert.assertTrue(wizardAction.getBackBtn().isEnabled());
		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());
		Assert.assertTrue(wizardAction.getCancelBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkWithBundleInfoInitialState() {
		project.prepareServer();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		Assert.assertTrue(wizardAction.importLiferayWorkspace.workspaceLocation().isActive());
		Assert.assertFalse(wizardAction.importLiferayWorkspace.serverName().isActive());

		Assert.assertEquals(SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, wizardAction.getValidationMsg(3));

		Assert.assertEquals("gradle-liferay-workspace", wizardAction.importLiferayWorkspace.buildType().getText());

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkWithoutBundleInfoInitialState() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		Assert.assertEquals(SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, wizardAction.getValidationMsg(2));

		Assert.assertEquals("gradle-liferay-workspace", wizardAction.importLiferayWorkspace.buildType().getText());

		Assert.assertFalse(wizardAction.importLiferayWorkspace.downloadLiferayBundle().isChecked());

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.importLiferayWorkspace.downloadLiferayBundle().select();
		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());
		wizardAction.importLiferayWorkspace.downloadLiferayBundle().deselect();

		wizardAction.cancel();
	}

	@Test
	public void checkWorkspaceLocation() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "import-liferay-workspace-wizard-location.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.importLiferayWorkspace.prepareLocation(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Rule
	public ImportLiferayWorkspaceProjectSupport project = new ImportLiferayWorkspaceProjectSupport(
		bot, "test-liferay-workspace-gradle");

}