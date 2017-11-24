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
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class ValidationNewLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void checkBuildType() {
	}

	@Test
	public void checkBundleUrl() {
	}

	@Test
	public void checkExsitingLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = "test";

		wizardAction.prepareLiferayWorkspace(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspace(projectName);

		Assert.assertEquals(
			A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, _newLiferayWorkspaceProjectWizard.getValidationMsg());

		wizardAction.cancel();

		viewAction.closeAndDeleteProject(projectName);
	}

	@Ignore
	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_ENTER_THE_WORKSPACE_NAME, _newLiferayWorkspaceProjectWizard.getValidationMsg());
		Assert.assertEquals(StringPool.BLANK, _newLiferayWorkspaceProjectWizard.getProjectName().getText());

		Assert.assertTrue(_newLiferayWorkspaceProjectWizard.getUseDefaultLocation().isChecked());
		Assert.assertEquals(false, _newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().isChecked());

		_newLiferayWorkspaceProjectWizard.getUseDefaultLocation().deselect();

		//will add tests on there OS since '/' and '\'
		String exceptLocation = envAction.getEclipseWorkspacePath().toOSString();
		String actualLocation = _newLiferayWorkspaceProjectWizard.getLocation().getText();

		Assert.assertEquals(exceptLocation.replace("\\", "/"), actualLocation);

		_newLiferayWorkspaceProjectWizard.getUseDefaultLocation().select();

		_newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().select();
		_newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().deselect();

		wizardAction.cancel();
	}

	@Test
	public void checkLocation() {
	}

	@Test
	public void checkProjectName() {
		wizardAction.openNewLiferayWorkspaceWizard();

		for (ValidationMsg msg : envAction.getValidationMsgs(
				new File(envAction.getValidationFolder(), "new-liferay-workspace-wizard-project-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			_newLiferayWorkspaceProjectWizard.getProjectName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkServerName() {
	}

	private NewLiferayWorkspaceWizard _newLiferayWorkspaceProjectWizard = new NewLiferayWorkspaceWizard(bot);

}