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

package com.liferay.ide.functional.sdk.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ValidationPluginProjectTests extends SwtbotBase {

	@Test
	public void testDefaults() {
		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

		Assert.assertEquals(PLEASE_SELECT_AT_LEAST_ONE_PROJECT_TO_IMPORT, wizardAction.getValidationMsg(2));

		validationAction.assertEnabledTrue(wizardAction.projectFromExistSource.sdkDirectory());
		validationAction.assertEnabledTrue(wizardAction.projectFromExistSource.browseSdkDirectoryBtn());
		validationAction.assertEnabledTrue(wizardAction.projectFromExistSource.sdkVersion());

		validationAction.assertActiveTrue(wizardAction.projectFromExistSource.sdkDirectory());
		validationAction.assertActiveFalse(wizardAction.projectFromExistSource.browseSdkDirectoryBtn());
		validationAction.assertActiveFalse(wizardAction.projectFromExistSource.sdkVersion());

		validationAction.assertEnabledTrue(wizardAction.projectFromExistSource.selectAllBtn());
		validationAction.assertEnabledTrue(wizardAction.projectFromExistSource.deselectAllBtn());
		validationAction.assertEnabledTrue(wizardAction.projectFromExistSource.refreshBtn());

		wizardAction.cancel();
	}

}