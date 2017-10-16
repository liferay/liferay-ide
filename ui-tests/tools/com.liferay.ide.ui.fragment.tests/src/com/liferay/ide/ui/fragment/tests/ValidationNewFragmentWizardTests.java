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
import com.liferay.ide.ui.liferay.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.ui.liferay.util.ValidationMsg;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ValidationNewFragmentWizardTests extends SwtbotBase {

	@Test
	public void checkBuildType() {
		wizardAction.openNewFragmentWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};
		String[] buildTypes = _newFragmentWizard.getBuildTypes().items();

		int expectedLength = expectedBuildTypes.length;
		int length = buildTypes.length;

		Assert.assertEquals(expectedLength, length);

		for (int i = 0; i < buildTypes.length; i++) {
			Assert.assertTrue(buildTypes[i].equals(expectedBuildTypes[i]));
		}

		wizardAction.cancel();
	}

	@Test
	public void checkInfoInitialState() {
	}

	@Test
	public void checkInitialState() {
	}

	@Test
	public void checkLiferayRuntime() {
	}

	@Test
	public void checkLocation() {
	}

	@Test
	public void checkProjectName() {
		wizardAction.openNewFragmentWizard();

		for (ValidationMsg msg : envAction.getValidationMsgs(
				new File(envAction.getValidationFolder(), "new-fragment-wizard-project-name.csv"))) {

			_newFragmentWizard.getProjectName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void createFragmentWithoutRuntime() {
		String projectName = "test-fragment";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		Assert.assertFalse(_newFragmentWizard.nextBtn().isEnabled());

		wizardAction.openNewRuntimeWizardFragment();

		wizardAction.next();

		wizardAction.prepareLiferay7RuntimeInfo(envAction.getLiferayServerDir().toOSString());

		wizardAction.finish();

		Assert.assertTrue(_newFragmentWizard.nextBtn().isEnabled());

		_newFragmentWizard.cancel();
	}

	private static final NewFragmentWizard _newFragmentWizard = new NewFragmentWizard(bot);

}