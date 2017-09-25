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
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.util.ValidationMsg;
import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ValidationImportLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void checkInitialState() {
		wizardAction.openImportLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_SELECT_THE_WORKSPACE_LOCATION, _importLiferayWorkspaceProject.getValidationMsg());

		Assert.assertEquals(StringPool.BLANK, _importLiferayWorkspaceProject.getWorkspaceLocation().getText());
		Assert.assertEquals(StringPool.BLANK, _importLiferayWorkspaceProject.getBuildTypeText().getText());
		Assert.assertFalse(_importLiferayWorkspaceProject.getAddProjectToWorkingSet().isChecked());

		Assert.assertTrue(_importLiferayWorkspaceProject.backBtn().isEnabled());
		Assert.assertFalse(_importLiferayWorkspaceProject.nextBtn().isEnabled());
		Assert.assertFalse(_importLiferayWorkspaceProject.finishBtn().isEnabled());
		Assert.assertTrue(_importLiferayWorkspaceProject.cancelBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkProjectName() {
		wizardAction.openImportLiferayWorkspaceWizard();

		for (ValidationMsg msg : envAction.getValidationMsgs(
				new File(envAction.getValidationFolder(), "import-liferay-workspace-wizard-location.csv"))) {

			_importLiferayWorkspaceProject.getWorkspaceLocation().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	private ImportLiferayWorkspaceProjectWizard _importLiferayWorkspaceProject =
		new ImportLiferayWorkspaceProjectWizard(bot);

}