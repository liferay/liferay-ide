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

package com.liferay.ide.functional.fragment.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle72Support;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ValidationFragmentWithoutTomcatTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle72Support liferayWorkspace = new LiferayWorkspaceGradle72Support(bot);

	@Test
	public void createFragmentWithoutRuntime() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEquals(PLEASE_SET_A_VALID_LIFERAY_PORTAL_RUNTIME, wizardAction.getValidationMsg(2));

		wizardAction.cancel();
	}

	@Ignore("ignore because blade 3.10.0 does not support the creation of gradle standalone")
	@Test
	public void createFragmentWithoutRuntimeLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		String projectName = "test";

		wizardAction.newLiferayWorkspace.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEquals(PLEASE_SET_A_VALID_LIFERAY_PORTAL_RUNTIME, wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}