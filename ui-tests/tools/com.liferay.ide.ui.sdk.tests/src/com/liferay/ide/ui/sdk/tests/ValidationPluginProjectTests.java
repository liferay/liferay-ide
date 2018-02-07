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

package com.liferay.ide.ui.sdk.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ValidationPluginProjectTests extends SwtbotBase {

	@Test
	public void testDefaults() {
		wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

		Assert.assertEquals(PLEASE_SELECT_AT_LEAST_ONE_PROJECT_TO_IMPORT, wizardAction.getValidationMsg(2));

		Assert.assertTrue(wizardAction.projectFromExistSource.sdkDirectory().isEnabled());
		Assert.assertTrue(wizardAction.projectFromExistSource.browseSdkDirectoryBtn().isEnabled());
		Assert.assertTrue(wizardAction.projectFromExistSource.sdkVersion().isEnabled());

		Assert.assertTrue(wizardAction.projectFromExistSource.sdkDirectory().isActive());
		Assert.assertFalse(wizardAction.projectFromExistSource.browseSdkDirectoryBtn().isActive());
		Assert.assertFalse(wizardAction.projectFromExistSource.sdkVersion().isActive());

		Assert.assertTrue(wizardAction.projectFromExistSource.selectAllBtn().isEnabled());
		Assert.assertTrue(wizardAction.projectFromExistSource.deselectAllBtn().isEnabled());
		Assert.assertTrue(wizardAction.projectFromExistSource.refreshBtn().isEnabled());

		wizardAction.cancel();
	}

}