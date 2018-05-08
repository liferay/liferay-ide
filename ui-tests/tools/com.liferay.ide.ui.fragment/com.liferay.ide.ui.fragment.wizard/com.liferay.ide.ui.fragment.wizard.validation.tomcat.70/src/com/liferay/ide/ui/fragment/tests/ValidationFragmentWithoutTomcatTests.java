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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ValidationFragmentWithoutTomcatTests extends SwtbotBase {

	@Test
	public void createFragmentWithoutRuntime() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEquals(LIFERAY_RUNTIME_MUST_BE_CONFIGURED, wizardAction.getValidationMsg(2));

		wizardAction.cancel();
	}

	@Test
	public void createFragmentWithoutRuntimeLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = "test";

		wizardAction.newLiferayWorkspace.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEquals(LIFERAY_RUNTIME_MUST_BE_CONFIGURED, wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}