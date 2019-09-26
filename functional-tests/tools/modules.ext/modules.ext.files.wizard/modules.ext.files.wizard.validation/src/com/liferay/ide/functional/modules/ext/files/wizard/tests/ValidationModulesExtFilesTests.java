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

package com.liferay.ide.functional.modules.ext.files.wizard.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectsSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle72Support;
import com.liferay.ide.functional.swtbot.util.StringPool;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 */
public class ValidationModulesExtFilesTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle72Support liferayWorkspace = new LiferayWorkspaceGradle72Support(bot);

	@Test
	public void checkAvailableProjectNames() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(projects.getName(0));

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(projects.getName(1));

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.newModulesExtFiles.openFileMenuModulesExtFilesWizard();

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModulesExtFiles.moduleExtProjectName());

		String[] expectedProjectNames = {projects.getName(0), projects.getName(1)};
		String[] actualProjectNames = wizardAction.newModulesExtFiles.moduleExtProjectNames();

		validationAction.assertLengthEquals(expectedProjectNames, actualProjectNames);

		for (int i = 0; i < actualProjectNames.length; i++) {
			validationAction.assertEquals(expectedProjectNames[i], actualProjectNames[i]);
		}

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getName(), "ext", projects.getName(0));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getName(), "ext", projects.getName(1));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getName(), "ext");
	}

	@Test
	public void checkExistingOverridenFiles() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(projects.getName(0));

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.newModulesExtFiles.openFileMenuModulesExtFilesWizard();

		wizardAction.newModulesExtFiles.selectModuleExtProjectName(projects.getName(0));

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("portlet.properties");

		validationAction.assertEquals(ADD_SOURCE_FILES_TO_OVERRIDE, wizardAction.getValidationMsg(1));

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("portlet.properties");

		validationAction.assertEquals(
			MULTIPLE_OCCURRENCE_OF_PORTLET_PROPERTIES_WERE_FOUND, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getName(), "ext");
	}

	@Test
	public void checkExtFilesWithoutProjects() {
		wizardAction.newModulesExtFiles.openFileMenuModulesExtFilesWizard();

		validationAction.assertEquals(MODULE_EXT_PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(1));

		validationAction.assertEquals(null, wizardAction.newModulesExtFiles.moduleExtProjectNames());

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModulesExtFiles.origialModule());

		validationAction.assertEnabledTrue(wizardAction.newModulesExt.getAddOriginMoudleBtn());

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		validationAction.assertEquals(UNABLE_TO_GET_SOURCE_FILES_IN_CURRENT_CONTEXT, dialogAction.getValidationMsg(0));

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		validationAction.assertEnabledTrue(wizardAction.getBackBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		validationAction.assertEnabledTrue(wizardAction.getCancelBtn());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkInfoInitialState() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(projects.getName(0));

		validationAction.assertEnabledTrue(wizardAction.newModulesExt.browseBtn());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.newModulesExt.selectLaunchModulesExtFiles();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		validationAction.assertEquals(ADD_SOURCE_FILES_TO_OVERRIDE, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledTrue(wizardAction.newModulesExt.getAddOriginMoudleBtn());

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		validationAction.assertEnabledTrue(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getName(), "ext");
	}

	@Test
	public void checkOverrideFileOnNoSourceModule() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(projects.getName(0));

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.caucho");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openProjectNewModuleExtFilesWizard(liferayWorkspace.getName(), projects.getName(0));

		validationAction.assertEquals(MODULE_EXT_PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(1));

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		validationAction.assertEquals(UNABLE_TO_GET_SOURCE_FILES_IN_CURRENT_CONTEXT, dialogAction.getValidationMsg(0));

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getName(), "ext");
	}

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}