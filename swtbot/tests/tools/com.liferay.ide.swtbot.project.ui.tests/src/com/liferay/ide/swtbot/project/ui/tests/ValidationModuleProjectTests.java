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

package com.liferay.ide.swtbot.project.ui.tests;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.swtbot.liferay.ui.util.ValidationMsg;
import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 */
public class ValidationModuleProjectTests extends SwtbotBase {

	@Test
	public void validateComponentClassAndPackageName() {
		String projectName = "My-test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModule(projectName);

		wizardAction.next();

		Assert.assertTrue(_newModuleInfoWizard.finishBtn().isEnabled());
		Assert.assertEquals(CONFIGURE_COMPONENT_CLASS, _newModuleInfoWizard.getValidationMsg());

		wizardAction.prepareLiferayModuleInfo(projectName, StringPool.BLANK);
		Assert.assertEquals(INVALID_CLASS_NAME, _newModuleInfoWizard.getValidationMsg());

		wizardAction.prepareLiferayModuleInfo(StringPool.BLANK, "!!");
		Assert.assertEquals(INVALID_PACKAGE_NAME, _newModuleInfoWizard.getValidationMsg());

		_newModuleProjectWizard.cancel();
	}

	@Test
	public void validateProjectName() {
		wizardAction.openNewLiferayModuleWizard();
		Assert.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, _newModuleProjectWizard.getValidationMsg());
		Assert.assertFalse(_newModuleProjectWizard.finishBtn().isEnabled());

		for (ValidationMsg msg : getValidationMsgs(
				new File(envAction.getValidationFolder(), "new-liferay-module-project-wizard-project-name.csv"))) {

			_newModuleProjectWizard.getProjectName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		_newModuleProjectWizard.cancel();
	}

	@Test
	public void validateProperties() {
		String projectName = "My-test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModule(projectName);

		wizardAction.next();

		wizardAction.prepareLiferayModuleInfoProperties(StringPool.BLANK, StringPool.BLANK);
		Assert.assertEquals(NAME_MUST_BE_SPECIFIED, _newModuleInfoWizard.getValidationMsg());
		_newModuleInfoWizard.getDeleteBtn().click();

		wizardAction.prepareLiferayModuleInfoProperties(StringPool.BLANK, projectName);
		Assert.assertEquals(NAME_MUST_BE_SPECIFIED, _newModuleInfoWizard.getValidationMsg());
		_newModuleInfoWizard.getDeleteBtn().click();

		wizardAction.prepareLiferayModuleInfoProperties(projectName, StringPool.BLANK);
		Assert.assertEquals(VALUE_MUST_BE_SPECIFIED, _newModuleInfoWizard.getValidationMsg());
		_newModuleInfoWizard.getDeleteBtn().click();

		wizardAction.prepareLiferayModuleInfoProperties(projectName, projectName);
		Assert.assertEquals(CONFIGURE_COMPONENT_CLASS, _newModuleInfoWizard.getValidationMsg());

		wizardAction.cancel();
	}

	private static final NewLiferayModuleInfoWizard _newModuleInfoWizard = new NewLiferayModuleInfoWizard(bot);
	private static final NewLiferayModuleWizard _newModuleProjectWizard = new NewLiferayModuleWizard(bot);

}