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

package com.liferay.ide.ui.module.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Rui Wang
 */
public class ValidationModuleProjectTests extends SwtbotBase {

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

	@Ignore
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
	public void validatemoduleprojectTemplate() {
		String projectName = "test-template";

		String[] templates = {
			ACTIVATOR, API, CONTENT_TARGETING_REPORT, CONTENT_TARGETING_RULE, CONTENT_TARGETING_TRACKING_ACTION,
			CONTROL_MENU_ENTRY, FORM_FIELD, FREEMARKER_PORTLET, LAYOUT_TEMPLATE, MVC_PORTLET, NPM_ANGULAR_PORTLET,
			NPM_BILLBOARDJS_PORLET, NPM_ISOMORPHIC_PORTLET, NPM_JQUERY_PORTLET, NPM_METALJS_PORTLET, NPM_PORTLET,
			NPM_REACT_PORTLET, NPM_VUEJS_PORTLET, PANEL_APP, PORTLET, PORTLET_CONFIGURATION_ICON, PORTLET_PROVIDER,
			PORTLET_TOOLBAR_CONTRIBUTOR, REST, SERVICE, SERVICE_BUILDER, SERVICE_WRAPPER, SIMULATION_PANEL_ENTRY,
			SOCIAL_BOOKMARK, SOY_PORTLET, SPRING_MVC_PORTLET, TEMPLATE_CONTEXT_CONCONTRIBUTOR, THEME, THEME_CONTRIBUTOR,
			WAR_HOOK, WAR_MVC_PORTLET
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

}