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
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayJsfProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class ValidationModuleProjectTests extends SwtbotBase {

	@Test
	public void validateProjectName() {
		Assert.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, _newJsfProjectWizard.getValidationMsg());
		Assert.assertFalse(_newJsfProjectWizard.finishBtn().isEnabled());

		_newJsfProjectWizard.getProjectName().setText(".");
		sleep();
		Assert.assertEquals(" '.'" + IS_AN_INVALID_NAME_ON_PLATFORM, _newJsfProjectWizard.getValidationMsg());
		Assert.assertFalse(_newJsfProjectWizard.finishBtn().isEnabled());

		_newJsfProjectWizard.getProjectName().setText("/");
		sleep();
		Assert.assertEquals(
			" /" + IS_AN_INVALID_CHARACTER_IN_RESOURCE_NAME + "'/'.", _newJsfProjectWizard.getValidationMsg());
		Assert.assertFalse(_newJsfProjectWizard.finishBtn().isEnabled());

		_newJsfProjectWizard.getProjectName().setText("$");
		sleep();
		Assert.assertEquals(THE_PROJECT_NAME_IS_INVALID, _newJsfProjectWizard.getValidationMsg());
		Assert.assertFalse(_newJsfProjectWizard.finishBtn().isEnabled());

		_newJsfProjectWizard.getProjectName().setText(StringPool.BLANK);
		sleep();
		Assert.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, _newJsfProjectWizard.getValidationMsg());
		Assert.assertFalse(_newJsfProjectWizard.finishBtn().isEnabled());

		_newJsfProjectWizard.getProjectName().setText("a");
		sleep();
		Assert.assertEquals(
			ENTER_A_NAME_AND_CHOOSE_TEMPLATE_FOR_NEW_JSF_PROJECT, _newJsfProjectWizard.getValidationMsg());
		Assert.assertTrue(_newJsfProjectWizard.finishBtn().isEnabled());

		_newJsfProjectWizard.cancel();
	}

	@Test
	public void validationTheSecondPage() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModule("test");

		wizardAction.next();

		wizardAction.prepareLiferayModuleInfo("@@", StringPool.BLANK);

		Assert.assertEquals(INVALID_CLASS_NAME, _newModuleInfoWizard.getValidationMsg());

		wizardAction.prepareLiferayModuleInfo(StringPool.BLANK, "!!");

		Assert.assertEquals(INVALID_CLASS_NAME, _newModuleInfoWizard.getValidationMsg());

		wizardAction.prepareLiferayModuleInfo("testClassName", "testPackageName");

		_newModuleInfoWizard.getAddPropertyKeyBtn().click();
		sleep();
		_newModuleInfoWizard.getProperties().setFocus();
		Assert.assertEquals(NAME_MUST_BE_SPECIFIED, _newModuleInfoWizard.getValidationMsg());
		Assert.assertTrue(_newModuleInfoWizard.getDeleteBtn().isEnabled());
		_newModuleInfoWizard.getDeleteBtn().click();

		_newModuleInfoWizard.getAddPropertyKeyBtn().click();
		sleep();
		_newModuleInfoWizard.getProperties().setText(2, "a");
		_newModuleInfoWizard.getProperties().setFocus();
		sleep();
		Assert.assertEquals(VALUE_MUST_BE_SPECIFIED, _newModuleInfoWizard.getValidationMsg());
		sleep(2000);
		_newModuleInfoWizard.getProperties().doubleClick(0, 1);
		sleep();
		_newModuleInfoWizard.getProperties().setText(2, "b");

		wizardAction.cancel();
	}

	private static final NewLiferayJsfProjectWizard _newJsfProjectWizard = new NewLiferayJsfProjectWizard(bot);
	private static final NewLiferayModuleInfoWizard _newModuleInfoWizard = new NewLiferayModuleInfoWizard(bot);

}