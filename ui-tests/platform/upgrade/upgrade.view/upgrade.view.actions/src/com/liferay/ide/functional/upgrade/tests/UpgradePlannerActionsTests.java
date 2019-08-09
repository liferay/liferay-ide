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
import com.liferay.ide.ui.liferay.support.project.ProjectsSupport;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 * @author Ashley Yuan
 */
public class UpgradePlannerActionsTests extends SwtbotBase {

	@Test
	public void expandAndCollapse() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(project.getName(), UPGRADING_CODE_TO_PRODUCT_VER, "6.2", "7.1");

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
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(project.getName(), UPGRADING_CODE_TO_PRODUCT_VER, "6.2", "7.1");

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
		viewAction.upgradePlan.showProgressView();

		validationAction.assertViewVisible(PROGRESS);
	}

	@Test
	public void showUpgradePlanInfoView() {
		viewAction.upgradePlan.showUpgradePlanInfoView();

		validationAction.assertViewVisible(LIFERAY_UPGRADE_PLAN_INFO);
	}

	@Test
	public void switchUpgradePlan() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(projects.getName(0), UPGRADING_CODE_TO_PRODUCT_VER, "6.2", "7.1");

		wizardAction.finish();

		viewAction.upgradePlan.clickSwitchUpgradePlan();

		validationAction.assertEnabledFalse(dialogAction.switchUpgradePlan.getStartPlanBtn());

		validationAction.assertEnabledFalse(dialogAction.switchUpgradePlan.getRemovePlanBtn());

		dialogAction.switchUpgradePlan.close();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(projects.getName(1), UPGRADING_TO_PRODUCT_VER, "7.0", "7.2");

		wizardAction.finish();

		validationAction.assertContains(projects.getName(1), viewAction.upgradePlan.getViewTitle());

		viewAction.upgradePlan.clickSwitchUpgradePlan();

		String[] firstUpgradePlan = {projects.getName(0), "6.2", "7.1", UPGRADING_CODE_TO_PRODUCT_VER_URL};
		String[] secondUpgradePlan = {projects.getName(1), "7.0", "7.2", UPGRADING_TO_PRODUCT_VER_URL};

		validationAction.assertEquals(
			firstUpgradePlan, dialogAction.switchUpgradePlan.getUpgradePlan(projects.getName(0)));

		validationAction.assertEquals(
			secondUpgradePlan, dialogAction.switchUpgradePlan.getUpgradePlan(projects.getName(1)));

		dialogAction.switchUpgradePlan.chooseUpgradePlan(projects.getName(0));

		validationAction.assertEnabledTrue(dialogAction.switchUpgradePlan.getStartPlanBtn());

		validationAction.assertEnabledTrue(dialogAction.switchUpgradePlan.getRemovePlanBtn());

		dialogAction.switchUpgradePlan.startPlan();

		validationAction.assertContains(projects.getName(0), viewAction.upgradePlan.getViewTitle());

		dialogAction.switchUpgradePlan.selectUpgradePlan(projects.getName(1));

		validationAction.assertContains(projects.getName(1), viewAction.upgradePlan.getViewTitle());

		dialogAction.switchUpgradePlan.chooseUpgradePlan(projects.getName(0));

		dialogAction.switchUpgradePlan.removePlan();

		Assert.assertFalse(dialogAction.switchUpgradePlan.containsUpgradePlan(projects.getName(0)));

		dialogAction.switchUpgradePlan.close();
	}

	@Test
	public void upgradePlanDetails() {
		viewAction.upgradePlan.clickUpgradePlanDetails();

		dialogAction.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot) {

		@Override
		public boolean isSwitchToUpgradePespective() {
			return true;
		}

	};

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}