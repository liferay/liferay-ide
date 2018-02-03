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
import com.liferay.ide.ui.liferay.page.wizard.project.ImportLiferayWorkspaceWizard;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ValidationImportLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void checkInitialState() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_SELECT_THE_WORKSPACE_LOCATION, _importLiferayWorkspace.getValidationMsg());

		Assert.assertEquals(StringPool.BLANK, _importLiferayWorkspace.getWorkspaceLocation().getText());
		Assert.assertEquals(StringPool.BLANK, _importLiferayWorkspace.getBuildTypeText().getText());
		Assert.assertFalse(_importLiferayWorkspace.getAddProjectToWorkingSet().isChecked());

		Assert.assertTrue(_importLiferayWorkspace.backBtn().isEnabled());
		Assert.assertFalse(_importLiferayWorkspace.nextBtn().isEnabled());
		Assert.assertFalse(_importLiferayWorkspace.finishBtn().isEnabled());
		Assert.assertTrue(_importLiferayWorkspace.cancelBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkProjectName() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "import-liferay-workspace-wizard-location.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			_importLiferayWorkspace.getWorkspaceLocation().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	private ImportLiferayWorkspaceWizard _importLiferayWorkspace = new ImportLiferayWorkspaceWizard(bot);

}