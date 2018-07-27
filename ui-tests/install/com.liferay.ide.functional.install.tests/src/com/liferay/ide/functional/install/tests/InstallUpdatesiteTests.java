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

import org.eclipse.swtbot.swt.finder.SWTBotAssert;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class InstallUpdatesiteTests extends SwtbotBase {

	@Test
	public void installUpdatesite() {
		String updatesiteUrl = System.getProperty("liferay.ide.repo");

		wizardAction.openInstallNewSoftwareWizard();

		Assert.assertEquals(SELECT_A_SITE_OR_ENTER_THE_LOCATION_OF_A_SITE, wizardAction.getValidationMsg(1));
		Assert.assertFalse(wizardAction.getBackBtn().isEnabled());
		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.installNewSoftware.addRepository();

		dialogAction.addRepository.addLocation(updatesiteUrl);

		Assert.assertEquals(CHECK_THE_ITEMS_THAT_YOU_WISH_TO_INSTALL, wizardAction.getValidationMsg(1));

		wizardAction.installNewSoftware.selectAll();

		wizardAction.installNewSoftware.contactAllUpdateSites().deselect();

		wizardAction.next();

		ide.sleep(2500);

		Assert.assertEquals(YOUR_ORIGINAL_REQUEST_HAS_BEEN_MODIFIED_SEE_THE_DETAILS, wizardAction.getValidationMsg(1));
		Assert.assertTrue(wizardAction.getBackBtn().isEnabled());

		wizardAction.next();

		SWTBotAssert.assertContains(LICENSES_MUST_BE_REVIEWED,wizardAction.getValidationMsg(1));
		SWTBotAssert.assertContains(BEFORE_THE_SOFTWARE_CAN_BE_INSTALLED,wizardAction.getValidationMsg(1));
		Assert.assertFalse(wizardAction.getNextBtn().isEnabled());
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.installNewSoftware.selectAcceptTerms();

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

}