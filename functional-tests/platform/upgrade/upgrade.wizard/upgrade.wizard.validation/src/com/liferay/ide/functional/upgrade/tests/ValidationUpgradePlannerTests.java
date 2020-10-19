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
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.util.StringPool;

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

		String[] expectedCurrentVersions = {"6.2", "7.0", "7.1", "7.2"};

		ComboBox currentVersionComboBox = wizardAction.newUpgradePlan.currentLiferayVersion();

		String[] currentVersions = currentVersionComboBox.items();

		validationAction.assertLengthEquals(expectedCurrentVersions, currentVersions);

		for (int i = 0; i < currentVersions.length; i++) {
			validationAction.assertEquals(expectedCurrentVersions[i], currentVersions[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void checkCurrentVersionSameWithTargetVersion() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(project.getName(), CODE_UPGRADE, "7.0", "7.0");

		validationAction.assertEquals(TARGET_VERSION_GREATER_THAN_CURRENT_VERSION, wizardAction.getValidationMsg(1));

		wizardAction.cancel();
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		validationAction.assertEquals(CREATE_AND_START_A_NEW_UPGRADE_PLAN, wizardAction.getValidationMsg(1));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newUpgradePlan.name());

		validationAction.assertEquals(CODE_UPGRADE, wizardAction.newUpgradePlan.getUpgradePlanOutline());

		validationAction.assertEquals("6.2", wizardAction.newUpgradePlan.getCurrentLiferayVersion());

		validationAction.assertEquals("7.3", wizardAction.newUpgradePlan.getTargetLiferayVersion());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkName() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepareUpgradePlanOutline(CODE_UPGRADE);

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

		String[] expectedTargetVersions = {"7.0", "7.1", "7.2", "7.3"};

		ComboBox targetVersionComboBox = wizardAction.newUpgradePlan.targetLiferayVersion();

		String[] targetVersions = targetVersionComboBox.items();

		validationAction.assertLengthEquals(expectedTargetVersions, targetVersions);

		for (int i = 0; i < targetVersions.length; i++) {
			validationAction.assertEquals(expectedTargetVersions[i], targetVersions[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void checkUpgradePlanOutline() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepareName(project.getName());

		validationAction.assertEquals(CREATE_AND_START_A_NEW_UPGRADE_PLAN, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		String[] expectedUpgradePlanOutlines = {CODE_UPGRADE};

		ComboBox upgradePlanOutlineComboBox = wizardAction.newUpgradePlan.upgradePlanOutline();

		String[] upgradePlanOutlines = upgradePlanOutlineComboBox.items();

		validationAction.assertLengthEquals(expectedUpgradePlanOutlines, upgradePlanOutlines);

		for (int i = 0; i < upgradePlanOutlines.length; i++) {
			validationAction.assertEquals(expectedUpgradePlanOutlines[i], upgradePlanOutlines[i]);
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