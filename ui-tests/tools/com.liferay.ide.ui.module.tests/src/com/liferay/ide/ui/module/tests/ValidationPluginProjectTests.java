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

package com.liferay.ide.ui.module.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.page.wizard.LiferayProjectFromExistSourceWizard;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ValidationPluginProjectTests extends SwtbotBase {

	@Test
	public void testDefaults() {
		wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

		Assert.assertEquals(PLEASE_SELECT_AT_LEAST_ONE_PROJECT_TO_IMPORT, _pluginFromSourceWizard.getValidationMsg());

		Assert.assertTrue(_pluginFromSourceWizard.getSdkDirectory().isEnabled());
		Assert.assertTrue(_pluginFromSourceWizard.getBrowseSdkDirectoryBtn().isEnabled());
		Assert.assertTrue(_pluginFromSourceWizard.getSdkVersion().isEnabled());

		Assert.assertTrue(_pluginFromSourceWizard.getSdkDirectory().isActive());
		Assert.assertFalse(_pluginFromSourceWizard.getBrowseSdkDirectoryBtn().isActive());
		Assert.assertFalse(_pluginFromSourceWizard.getSdkVersion().isActive());

		Assert.assertTrue(_pluginFromSourceWizard.getSelectAllBtn().isEnabled());
		Assert.assertTrue(_pluginFromSourceWizard.getDeselectAllBtn().isEnabled());
		Assert.assertTrue(_pluginFromSourceWizard.getRefreshBtn().isEnabled());

		wizardAction.cancel();
	}

	private static final LiferayProjectFromExistSourceWizard _pluginFromSourceWizard =
		new LiferayProjectFromExistSourceWizard(bot);

}