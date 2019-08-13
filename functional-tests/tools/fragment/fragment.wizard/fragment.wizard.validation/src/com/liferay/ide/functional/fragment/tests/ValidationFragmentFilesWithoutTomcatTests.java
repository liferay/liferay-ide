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
import com.liferay.ide.functional.swtbot.util.StringPool;

import org.junit.Test;

/**
 * @author Lily Li
 */
public class ValidationFragmentFilesWithoutTomcatTests extends SwtbotBase {

	@Test
	public void checkInitialStateWithoutRuntime() {
		wizardAction.openFileMenuFragmentFilesWizard();

		validationAction.assertEquals(NO_SUITABLE_LIFERAY_FRAGMENT_PROJECT, wizardAction.getValidationMsg(1));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newFragmentFiles.projectName());

		validationAction.assertTextEquals("<None>", wizardAction.newFragmentFiles.runtimeName());

		validationAction.assertEnabledTrue(wizardAction.newFragmentFiles.newRuntimeBtn());

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newFragmentFiles.hostOsgiBundle());

		validationAction.assertEnabledFalse(wizardAction.newFragmentFiles.addOverrideFilesBtn());

		validationAction.assertEnabledFalse(wizardAction.newFragmentFiles.deleteBtn());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

}