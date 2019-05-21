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
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class UpgradePlannerIconsTests extends SwtbotBase {

	@Test
	public void expandAndcollapse() {
		viewAction.switchUpgradePlannerPerspective();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(project.getName(), "07-liferay-code-upgrade-plan", "6.2", "7.1");

		wizardAction.newUpgradePlan.prepareCurrentCodeLocation(StringPool.BLANK);

		wizardAction.finish();

		viewAction.upgradePlan.clickExpandAll();

		validationAction.assertTreeItemExpanded(
			viewAction.upgradePlan.getTreeItem(UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT));

		viewAction.upgradePlan.clickCollapseAll();

		validationAction.assertTreeItemCollapsed(
			viewAction.upgradePlan.getTreeItem(UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT));
	}

	@Test
	public void restartUpgradePlan() {
		viewAction.switchUpgradePlannerPerspective();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(project.getName(), "07-liferay-code-upgrade-plan", "6.2", "7.1");

		wizardAction.newUpgradePlan.prepareCurrentCodeLocation(StringPool.BLANK);

		wizardAction.finish();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE, CREATE_NEW_LIFERAY_WORKSPACE);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.clickRestartUpgradePlan();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE, CREATE_NEW_LIFERAY_WORKSPACE);

		Assert.assertTrue(viewAction.upgradePlan.checkVisible(SKIP));
	}

	@Test
	public void showProgressView() {
		viewAction.switchUpgradePlannerPerspective();

		viewAction.upgradePlan.showProgressView();

		validationAction.assertViewVisible(PROGRESS);
	}

	@Test
	public void showUpgradePlanInfoView() {
		viewAction.switchUpgradePlannerPerspective();

		viewAction.upgradePlan.showUpgradePlanInfoView();

		validationAction.assertViewVisible(LIFERAY_UPGRADE_PLAN_INFO);
	}

	@Test
	public void switchUpgradePlan() {
		viewAction.switchUpgradePlannerPerspective();

		viewAction.upgradePlan.clickSwitchUpgradePlan();

		dialogAction.close();
	}

	@Test
	public void upgradePlanDetails() {
		viewAction.switchUpgradePlannerPerspective();

		viewAction.upgradePlan.clickUpgradePlanDetails();

		dialogAction.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}