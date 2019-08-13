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

package com.liferay.ide.functional.upgrade.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;

import org.junit.Test;

/**
 * @author Lily Li
 */
public class UpgradePlannerShortcutsTests extends SwtbotBase {

	@Test
	public void checkDoubleClickOpenUpgradePlanShortcuts() {
		viewAction.switchUpgradePlannerPerspective();

		viewAction.upgradePlan.clickUpgradePlanDetails();

		dialogAction.confirm();

		viewAction.upgradePlan.doubleClickOpenUpgradePlanWizard(
			NO_UPGRADE_STEPS_DOUBLE_CLICK_TO_START_A_NEW_UPGRADE_PLAN);

		wizardAction.cancel();
	}

	@Test
	public void checkNewUpgradePlanShortcuts() {
		viewAction.switchUpgradePlannerPerspective();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.cancel();
	}

}