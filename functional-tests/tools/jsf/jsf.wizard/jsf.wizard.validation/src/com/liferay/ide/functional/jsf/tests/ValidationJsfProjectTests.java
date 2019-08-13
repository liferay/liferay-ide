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

package com.liferay.ide.functional.jsf.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.project.ProjectsSupport;
import com.liferay.ide.functional.liferay.util.ValidationMsg;
import com.liferay.ide.functional.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ValidationJsfProjectTests extends SwtbotBase {

	@Test
	public void checkDefaultLocation() {
		String exceptLocation = envAction.getEclipseWorkspacePathOSString();

		if ("win32".equals(Platform.getOS())) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.deselectUseDefaultLocation();

		ide.sleep();

		wizardAction.newLiferayJsf.prepareGradle(project.getName());

		validationAction.assertEquals(LOCATION_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		validationAction.assertEquals(StringPool.BLANK, wizardAction.newLiferayJsf.getLocation());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.newLiferayJsf.prepareGradle(StringPool.BLANK);

		validationAction.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newLiferayJsf.prepareGradle(project.getName());

		wizardAction.newLiferayJsf.selectUseDefaultLocation();

		ide.sleep();

		validationAction.assertEquals(exceptLocation, wizardAction.newLiferayJsf.getLocation());

		wizardAction.newLiferayJsf.prepareGradle(StringPool.BLANK);

		validationAction.assertEquals(PROJECT_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		validationAction.assertEquals(exceptLocation, wizardAction.newLiferayJsf.getLocation());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayJsfProjectWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newLiferayJsf.projectName());

		validationAction.assertCheckedTrue(wizardAction.newLiferayJsf.useDefaultLocation());

		validationAction.assertEquals(StringPool.BLANK, wizardAction.newLiferayJsf.getLocation());

		validationAction.assertTextEquals(JSF_STANDARD, wizardAction.newLiferayJsf.projectComponentSuite());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		validationAction.assertEnabledTrue(wizardAction.getCancelBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkLocationWithWorkspace() {
		String exceptLocation = envAction.getEclipseWorkspacePathOSString();

		if ("win32".equals(Platform.getOS())) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(projects.getName(0));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(projects.getName(1));

		ide.sleep();

		validationAction.assertEquals(exceptLocation, wizardAction.newLiferayJsf.getLocation());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Ignore("ignore for moving to integration in IDE-4451")
	@Test
	public void checkWaitGradleBackgroundJobMessage() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(projects.getName(0));

		wizardAction.finish();

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(projects.getName(1));

		validationAction.assertEquals(
			PROJECT_WONT_BE_RESOLVED_COMPLETELY_UNTIL_ALL_GRADLE_BACKGROUND_JOBS_FINISH,
			wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void validateBuildType() {
		wizardAction.openNewLiferayJsfProjectWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};
		String[] buildTypes = wizardAction.newLiferayJsf.buildTypes();

		validationAction.assertLengthEquals(expectedBuildTypes, buildTypes);

		for (int i = 0; i < buildTypes.length; i++) {
			validationAction.assertEquals(expectedBuildTypes[i], buildTypes[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void validateJsfProjectComponentSuite() {
		String[] templates = {LIFERAY_FACES_ALLOY, ICEFACES, JSF_STANDARD, PRIMEFACES, RICHFACES};

		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName());

		validationAction.assertEquals(templates, wizardAction.newLiferayJsf.projectComponentSuite());

		wizardAction.cancel();
	}

	@Test
	public void validateProjectName() {
		wizardAction.openNewLiferayJsfProjectWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-jsf-project-wizard-project-name.csv"))) {

			wizardAction.newModule.prepareProjectName(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}