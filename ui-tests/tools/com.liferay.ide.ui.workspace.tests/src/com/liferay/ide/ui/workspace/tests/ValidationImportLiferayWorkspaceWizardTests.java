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
 */
public class ValidationImportLiferayWorkspaceWizardTests extends SwtbotBase {

	@Test
	public void checkInitialState() {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		Assert.assertEquals(PLEASE_SELECT_THE_WORKSPACE_LOCATION, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, wizardAction.importLiferayWorkspace.workspaceLocation().getText());
		Assert.assertEquals(StringPool.BLANK, wizardAction.importLiferayWorkspace.buildType().getText());
		Assert.assertFalse(wizardAction.importLiferayWorkspace.addProjectToWorkingSet().isChecked());

		Assert.assertTrue(wizardAction.getBackBtn().isEnabled());
		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());
		Assert.assertTrue(wizardAction.getCancelBtn().isEnabled());

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

			wizardAction.importLiferayWorkspace.prepareLocation(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

}