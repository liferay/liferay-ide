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

package com.liferay.ide.functional.install.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Test;

/**
 * @author Lily Li
 */
public class InstallUpdatesiteTests extends SwtbotBase {

	@Test
	public void installUpdatesite() {
		String updatesiteUrl = System.getProperty("liferay.ide.repo");

		wizardAction.openInstallNewSoftwareWizard();

		validationAction.assertEquals(SELECT_A_SITE_OR_ENTER_THE_LOCATION_OF_A_SITE, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.getBackBtn());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.installNewSoftware.addRepository();

		dialogAction.addRepository.addLocation(updatesiteUrl);

		validationAction.assertEquals(CHECK_THE_ITEMS_THAT_YOU_WISH_TO_INSTALL, wizardAction.getValidationMsg(1));

		ide.sleepLinux(1000);

		wizardAction.installNewSoftware.selectAll();

		wizardAction.installNewSoftware.deselectContactAllUpdateSites();

		wizardAction.next();

		ide.sleep(2500);

		ide.sleepLinux(2500);

		validationAction.assertEquals(
			YOUR_ORIGINAL_REQUEST_HAS_BEEN_MODIFIED_SEE_THE_DETAILS, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledTrue(wizardAction.getBackBtn());

		wizardAction.next();

		validationAction.assertContains(LICENSES_MUST_BE_REVIEWED, wizardAction.getValidationMsg(1));

		validationAction.assertContains(BEFORE_THE_SOFTWARE_CAN_BE_INSTALLED, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.installNewSoftware.selectAcceptTerms();

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

}