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
import com.liferay.ide.functional.liferay.support.project.ProjectsSupport;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class UpgradePlannerTests extends SwtbotBase {

	@Test
	public void testConfigureBundleURLPreviewAndPerform() {
		viewAction.switchLiferayWorkspacePerspective();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(projects.getName(0), "7.0");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.switchUpgradePlannerPerspective();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(projects.getName(1), UPGRADING_CODE_TO_PRODUCT_VER, "7.0", "7.1");

		wizardAction.finish();

		viewAction.upgradePlan.selectStep(UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS, CONFIGURE_BUNDLE_URL);

		viewAction.upgradePlan.clickToPreview();

		dialogAction.projectSelection.selectProject(projects.getName(0));

		dialogAction.confirm();

		validationAction.assertEditorVisible("Compare ('gradle.properties'-'gradle.properties-preview')");

		editorAction.close();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS, CONFIGURE_BUNDLE_URL);

		viewAction.upgradePlan.clickToPerform();

		dialogAction.projectSelection.selectProject(projects.getName(0));

		dialogAction.confirm();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(projects.getName(0), "gradle.properties");

		validationAction.assertContains("liferay-ce-portal-tomcat-7.1", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void testConfigureTargetPlatformVersionPreviewAndPerform() {
		viewAction.switchLiferayWorkspacePerspective();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(projects.getName(0), "7.0");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.switchUpgradePlannerPerspective();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(projects.getName(1), UPGRADING_CODE_TO_PRODUCT_VER, "7.0", "7.1");

		wizardAction.finish();

		viewAction.upgradePlan.selectStep(UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS, CONFIGURE_BUNDLE_URL);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS,
			CONFIGURE_TARGET_PLATFORM_VERSION);

		viewAction.upgradePlan.clickToPreview();

		dialogAction.projectSelection.selectProject(projects.getName(0));

		dialogAction.confirm();

		validationAction.assertEditorVisible("Compare ('gradle.properties'-'gradle.properties-preview')");

		editorAction.close();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS,
			CONFIGURE_TARGET_PLATFORM_VERSION);

		viewAction.upgradePlan.clickToPerform();

		dialogAction.projectSelection.selectProject(projects.getName(0));

		dialogAction.confirm();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(projects.getName(0), "gradle.properties");

		validationAction.assertContains("liferay.workspace.target.platform.version = 7.1", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void testInitializeServerBundlePerform() {
		viewAction.switchLiferayWorkspacePerspective();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(projects.getName(0), "7.0");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.switchUpgradePlannerPerspective();

		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare(projects.getName(1), UPGRADING_CODE_TO_PRODUCT_VER, "7.0", "7.1");

		wizardAction.finish();

		viewAction.upgradePlan.selectStep(UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS, CONFIGURE_BUNDLE_URL);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS,
			CONFIGURE_TARGET_PLATFORM_VERSION);

		viewAction.upgradePlan.clickSkip();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, CONFIGURE_LIFERAY_WORKSPACE_SETTINGS,
			INITIALIZE_SERVER_BUNDLE_REQUIRED);

		viewAction.upgradePlan.clickToPerform();

		dialogAction.projectSelection.selectProject(projects.getName(0));

		dialogAction.confirm();

		viewAction.showConsoleView();

		jobAction.waitForConsoleContent("[Gradle Operations]", "BUILD SUCCESSFUL", M1);

		Assert.assertTrue(viewAction.project.visibleFileTry(projects.getName(0), "bundles"));

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void testSetUpLiferayWorkspace() {
		wizardAction.openNewLiferayUpgradePlanWizard();

		wizardAction.newUpgradePlan.prepare("test", UPGRADING_CODE_TO_PRODUCT_VER, "6.2", "7.1");

		wizardAction.finish();

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE, CREATE_NEW_LIFERAY_WORKSPACE);

		viewAction.upgradePlan.clickToPerform();

		ide.sleep();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.visibleFileTry(project.getName());

		viewAction.upgradePlan.selectStep(
			UPGRADE_YOUR_DEVELOPMENT_ENVIRONMENT, SET_UP_LIFERAY_WORKSPACE, IMPORT_EXISTING_LIFERAY_WORKSPACE);

		viewAction.upgradePlan.clickSkip();

		viewAction.project.closeAndDelete(project.getName());
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