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

package com.liferay.ide.functional.modules.ext.validation.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.project.ProjectsSupport;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 * @author Ashley Yuan
 */
public class ValidationModulesExtTests extends SwtbotBase {

	@Test
	public void checkExtWithoutWorkspace() {
		wizardAction.openNewLiferayModulesExtWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.prepare(project.getName());

		validationAction.assertEquals(
			WE_RECOMMEND_LIFERAY_GRADLE_WORKSPACE_TO_DEVELOP_MODULE_EXT_PROJECT, wizardAction.getValidationMsg(4));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModulesExt.origialModuleName());

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModulesExt.origialModuleVersion());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkExtWithValidWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradleWithIndexSources(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.prepare(projects.getName(0));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModulesExt.origialModuleName());

		validationAction.assertEquals(ORIGINAL_MODULE_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(4));

		validationAction.assertEnabledTrue(wizardAction.newModulesExt.browseBtn());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.prepareText("*login");

		dialogAction.selectTableItem("com.liferay:com.liferay.login.web");

		validationAction.assertEnabledTrue(dialogAction.getConfirmBtn());

		dialogAction.confirm();

		validationAction.assertEnabledTrue(wizardAction.getNextBtn());

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void checkExtWithWorkspace70() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradleWithIndexSources(project.getName(), "7.0");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.prepare(projects.getName(0));

		validationAction.assertEquals(
			MODULE_EXT_PROJECTS_ONLY_WORK_ON_LIFERAY_WORKSPACE_WHICH_VERSION_IS_GREATER_THAN_70,
			wizardAction.getValidationMsg(4));

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void checkExtWithWorkspaceWithoutIndexSources() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.prepare(projects.getName(0));

		validationAction.assertEquals(ORIGINAL_MODULE_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.openSelectBrowseDialog();

		validationAction.assertEquals(
			THIS_FEATURE_ONLY_WORKS_WHEN_TARGET_PLATFORM_INDEX_SOURCES_IS_SET_TO_TRUE,
			dialogAction.getValidationMsg(0));

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void checkExtWithWorkspaceWithoutTargetPlatform() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradleWithoutTargetPlatform(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.prepare(projects.getName(0));

		validationAction.assertEquals(ORIGINAL_MODULE_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(4));

		wizardAction.newModulesExt.openSelectBrowseDialog();

		validationAction.assertEquals(NO_TARGET_PLATFORM_CONFIGURATION_DETECTED, dialogAction.getValidationMsg(0));

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void checkLocation() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradleWithIndexSources(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(projects.getName(0));

		wizardAction.newModulesExt.deselectUseDefaultLocation();

		String workspacePath = envAction.getEclipseWorkspacePathOSString();

		if ("win32".equals(Platform.getOS())) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		validationAction.assertTextEquals(workspacePath, wizardAction.newProject.location());

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-modules-ext-wizard-project-location.csv"))) {

			String os = msg.getOs();

			if (!os.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newModulesExt.prepareLocation(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(4));
		}

		wizardAction.newModulesExt.prepareGradle(project.getName(), workspacePath + "/testLocation");

		ide.sleep();

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("*login");

		dialogAction.selectTableItem("com.liferay:com.liferay.login.web:3.0.26");

		dialogAction.confirm();

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void checkModulesExtProjectName() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradleWithIndexSources(project.getName(), "7.2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-modules-ext-wizard-project-name.csv"))) {

			String os = msg.getOs();

			if (!os.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newModulesExt.prepareProjectName(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(4));
		}

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}