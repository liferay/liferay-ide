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

package com.liferay.ide.functional.module.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.project.ProjectsSupport;
import com.liferay.ide.functional.liferay.util.ValidationMsg;
import com.liferay.ide.functional.swtbot.page.ComboBox;
import com.liferay.ide.functional.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Rui Wang
 */
public class ValidationModuleProjectTests extends SwtbotBase {

	@Ignore("Ignore because of the change caused by IDE-4740")
	@Test
	public void checkDefaultVersionInLiferayWorkspace70() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "7.0");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		validationAction.assertEquals("7.0", wizardAction.newModule.defaultVersions());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("Ignore because of the change caused by IDE-4740")
	@Test
	public void checkDefaultVersionInLiferayWorkspace71() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "7.1");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		validationAction.assertEquals("7.1", wizardAction.newModule.defaultVersions());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayModuleWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModule.projectName());

		validationAction.assertCheckedTrue(wizardAction.newModule.useDefaultLocation());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		validationAction.assertEnabledTrue(wizardAction.getCancelBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkLiferayVersion() {
		wizardAction.openNewLiferayModuleWizard();

		String[] expectedLiferayVersions = {"7.0", "7.1", "7.2", "7.3"};

		ComboBox liferayVersionComboBox = wizardAction.newLiferayWorkspace.liferayVersion();

		String[] liferayVersions = liferayVersionComboBox.items();

		validationAction.assertLengthEquals(expectedLiferayVersions, liferayVersions);

		for (int i = 0; i < liferayVersions.length; i++) {
			validationAction.assertEquals(expectedLiferayVersions[i], liferayVersions[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void checkLocationWithWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(projects.getName(0));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		String exceptLocation = envAction.getEclipseWorkspacePathOSString();

		String workspaceModuleFolderLocation = exceptLocation + "/" + projects.getName(0) + "/modules";
		String workspaceWarFolderLocation = exceptLocation + "/" + projects.getName(0) + "/wars";

		if ("win32".equals(Platform.getOS())) {
			workspaceModuleFolderLocation = workspaceModuleFolderLocation.replaceAll("\\\\", "/");
			workspaceWarFolderLocation = workspaceWarFolderLocation.replaceAll("\\\\", "/");
		}

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(projects.getName(1), MVC_PORTLET);

		ide.sleep();

		validationAction.assertEquals(workspaceModuleFolderLocation, wizardAction.newModule.getLocation());

		wizardAction.newModule.prepareGradleInWorkspace(projects.getName(1), WAR_MVC_PORTLET);

		ide.sleep();

		validationAction.assertEquals(workspaceWarFolderLocation, wizardAction.newModule.getLocation());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projects.getName(0));
	}

	@Test
	public void validateBuildType() {
		wizardAction.openNewLiferayModuleWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};
		String[] buildTypes = wizardAction.newModule.buildTypes();

		validationAction.assertLengthEquals(expectedBuildTypes, buildTypes);

		for (int i = 0; i < buildTypes.length; i++) {
			validationAction.assertEquals(expectedBuildTypes[i], buildTypes[i]);
		}

		wizardAction.cancel();
	}

	@Test
	public void validateComponentClassAndPackageName() {
		String projectName = "test-validate";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(projectName);

		wizardAction.next();

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		validationAction.assertEquals(CONFIGURE_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.prepare(projectName, StringPool.BLANK);

		validationAction.assertEquals(INVALID_CLASS_NAME, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.prepare(StringPool.BLANK, "!!");

		validationAction.assertEquals(INVALID_PACKAGE_NAME, wizardAction.getValidationMsg(2));

		wizardAction.cancel();
	}

	@Test
	public void validateLoaction() {
		String projectName = "test-location";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.newModule.deselectUseDefaultLocation();

		String exceptLocation = envAction.getEclipseWorkspacePathOSString();
		String actualLocation = wizardAction.newModule.getLocation();

		if ("win32".equals(Platform.getOS())) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		validationAction.assertEquals(exceptLocation, actualLocation);

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-module-project-wizard-location.csv"))) {

			String env = msg.getOs();

			if (!env.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newModule.prepareLocation(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void validateModuleProjectTemplate() {
		String projectName = "test-template";

		String[] templates = {
			API, CONTROL_MENU_ENTRY, FORM_FIELD, JS_THEME, JS_WIDGET, LAYOUT_TEMPLATE, MVC_PORTLET, NPM_ANGULAR_PORTLET,
			NPM_REACT_PORTLET, NPM_VUEJS_PORTLET, PANEL_APP, PORTLET_CONFIGURATION_ICON, PORTLET_PROVIDER,
			PORTLET_TOOLBAR_CONTRIBUTOR, REST, SERVICE, SERVICE_BUILDER, SERVICE_WRAPPER, SIMULATION_PANEL_ENTRY,
			TEMPLATE_CONTEXT_CONCONTRIBUTOR, THEME, THEME_CONTRIBUTOR, WAR_CORE_EXT, WAR_HOOK, WAR_MVC_PORTLET
		};

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		validationAction.assertTextEquals(MVC_PORTLET, wizardAction.newModule.projectTemplateName());

		validationAction.assertEquals(templates, wizardAction.newModule.projectTemplateName());

		wizardAction.cancel();
	}

	@Test
	public void validateProjectName() {
		wizardAction.openNewLiferayModuleWizard();

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-module-project-wizard-project-name.csv"))) {

			wizardAction.newModule.prepareProjectName(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Test
	public void validateProperties() {
		String projectName = "test-validate";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.prepareProperties(StringPool.BLANK, StringPool.BLANK);

		validationAction.assertEquals(NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.clickDeleteBtn();

		wizardAction.newModuleInfo.prepareProperties(StringPool.BLANK, projectName);

		validationAction.assertEquals(NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.clickDeleteBtn();

		wizardAction.newModuleInfo.prepareProperties(projectName, StringPool.BLANK);

		validationAction.assertEquals(VALUE_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.clickDeleteBtn();

		wizardAction.newModuleInfo.prepareProperties(projectName, projectName);

		validationAction.assertEquals(CONFIGURE_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		validationAction.assertEnabledTrue(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void validateSecondPageInitialState() {
		String projectName = "test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(projectName);

		wizardAction.next();

		validationAction.assertEquals(CONFIGURE_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModuleInfo.componentClassName());

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newModuleInfo.packageName());

		wizardAction.cancel();
	}

	@Ignore("ignore because blade 3.10.0 template has changed")
	@Test
	public void validateTemplatesWithLiferayVersions() {
		String projectName = "test";

		wizardAction.openNewLiferayModuleWizard();

		String[] versions = {"7.0", "7.2"};

		for (String version : versions) {
			wizardAction.newModule.prepareGradle(projectName, SOCIAL_BOOKMARK, version);

			validationAction.assertEquals(
				SPECIFIED_LIFERAY_VERSION_IS_INVAILD_MUST_BE_IN_RANGE_710_720, wizardAction.getValidationMsg(2));

			validationAction.assertEnabledFalse(wizardAction.getFinishBtn());
		}

		String[] templates = {
			CONTENT_TARGETING_REPORT, CONTENT_TARGETING_RULE, CONTENT_TARGETING_TRACKING_ACTION, FORM_FIELD
		};

		for (String template : templates) {
			wizardAction.newModule.prepareGradle(projectName, template, "7.2");

			validationAction.assertEquals(
				SPECIFIED_LIFERAY_VERSION_IS_INVAILD_MUST_BE_IN_RANGE_700_720, wizardAction.getValidationMsg(2));

			validationAction.assertEnabledFalse(wizardAction.getFinishBtn());
		}

		String[] npmTemplates = {NPM_ANGULAR_PORTLET, NPM_REACT_PORTLET, NPM_VUEJS_PORTLET};

		for (String template : npmTemplates) {
			wizardAction.newModule.prepareGradle(projectName, template, "7.2");

			//update on blade 392
			validationAction.assertEquals(
				ENTER_A_NAME_AND_CHOOSE_A_TEMPLATE_FOR_A_NEW_LIFERAY_MODULE, wizardAction.getValidationMsg(2));

			validationAction.assertEnabledTrue(wizardAction.getFinishBtn());
		}

		wizardAction.cancel();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}