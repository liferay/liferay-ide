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

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.swtbot.page.ComboBox;
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 * @author Ashley Yuan
 */
public class ValidationUpgradePlannerTests extends SwtbotBase {

	@Test
	public void checkCurrentLifrayVersion() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		String[] expectedBuildTypes = {"6.2", "7.0", "7.1"};

		ComboBox buildTypeComboBox = wizardAction.newUpgradePlan.currentLiferayVersion();

		String[] buildTypes = buildTypeComboBox.items();

		validationAction.assertLengthEquals(expectedBuildTypes, buildTypes);

		for (int i = 0; i < buildTypes.length; i++) {
			validationAction.assertEquals(expectedBuildTypes[i], buildTypes[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void checkCurrentVersionSameWithTargetVersion() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(project.getName(), UPGRADING_CODE_TO_PRODUCT_VER, "7.0", "7.0");

		validationAction.assertEquals(TARGET_VERSION_GREATER_THAN_CURRENT_VERSION, wizardAction.getValidationMsg(1));

		wizardAction.cancel();
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		validationAction.assertEquals(CREATE_AND_START_A_NEW_UPGRADE_PLAN, wizardAction.getValidationMsg(1));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newUpgradePlan.name());

		validationAction.assertEquals(StringPool.BLANK, wizardAction.newUpgradePlan.getUpgradePlanOutline());

		validationAction.assertEquals("6.2", wizardAction.newUpgradePlan.getCurrentLiferayVersion());

		validationAction.assertEquals("7.2", wizardAction.newUpgradePlan.getTargetLiferayVersion());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkName() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepareUpgradePlanOutline(UPGRADING_CODE_TO_PRODUCT_VER);

		validationAction.assertEquals(CREATE_AND_START_A_NEW_UPGRADE_PLAN, wizardAction.getValidationMsg(1));

		wizardAction.newUpgradePlan.prepareName(project.getName());

		validationAction.assertEquals(CREATE_AND_START_A_NEW_UPGRADE_PLAN, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.newUpgradePlan.prepareName(StringPool.BLANK);

		validationAction.assertEquals(NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkTargetLiferayVersion() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		String[] expectedBuildTypes = {"7.0", "7.1", "7.2"};

		ComboBox buildTypeComboBox = wizardAction.newUpgradePlan.targetLiferayVersion();

		String[] buildTypes = buildTypeComboBox.items();

		validationAction.assertLengthEquals(expectedBuildTypes, buildTypes);

		for (int i = 0; i < buildTypes.length; i++) {
			validationAction.assertEquals(expectedBuildTypes[i], buildTypes[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void checkUpgradePlanOutline() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepareName(project.getName());

		validationAction.assertEquals(UPGRADE_PLAN_OUTLINE_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		String[] expectedBuildTypes = {UPGRADING_TO_PRODUCT_VER, UPGRADING_CODE_TO_PRODUCT_VER};

		ComboBox buildTypeComboBox = wizardAction.newUpgradePlan.upgradePlanOutline();

		String[] buildTypes = buildTypeComboBox.items();

		validationAction.assertLengthEquals(expectedBuildTypes, buildTypes);

		for (int i = 0; i < buildTypes.length; i++) {
			validationAction.assertEquals(expectedBuildTypes[i], buildTypes[i]);
		}

		wizardAction.cancel();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot) {

		@Override
		public boolean isSwitchToUpgradePespective() {
			return true;
		}

	};

}