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

package com.liferay.ide.ui.module.validation.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 * @author Ashley Yuan
 * @author Rui Wang
 */
public class ValidationModuleProjectTests extends SwtbotBase {

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayModuleWizard();

		Assert.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, wizardAction.newModule.projectName().getText());

		Assert.assertTrue(wizardAction.newModule.useDefaultLocation().isChecked());
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		Assert.assertTrue(wizardAction.getCancelBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void validateBuildType() {
		wizardAction.openNewLiferayModuleWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};
		String[] buildTypes = wizardAction.newModule.buildType().items();

		int expectedLength = expectedBuildTypes.length;
		int length = buildTypes.length;

		Assert.assertEquals(expectedLength, length);

		for (int i = 0; i < buildTypes.length; i++) {
			Assert.assertTrue(buildTypes[i].equals(expectedBuildTypes[i]));
		}

		wizardAction.cancel();
	}

	@Test
	public void validateComponentClassAndPackageName() {
		String projectName = "test-validate";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(projectName);

		wizardAction.next();

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());
		Assert.assertEquals(CONFIGURE_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.prepare(projectName, StringPool.BLANK);
		Assert.assertEquals(INVALID_CLASS_NAME, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.prepare(StringPool.BLANK, "!!");
		Assert.assertEquals(INVALID_PACKAGE_NAME, wizardAction.getValidationMsg(2));

		wizardAction.cancel();
	}

	@Ignore
	@Test
	public void validateLoaction() {
		String projectName = "test-location";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.newModule.deselectUseDefaultLocation();

		String exceptLocation = envAction.getEclipseWorkspacePath().toOSString();
		String actualLocation = wizardAction.newModule.location().getText();

		if (Platform.getOS().equals("win32")) {
			exceptLocation = exceptLocation.replaceAll("\\\\", "/");
		}

		Assert.assertEquals(exceptLocation, actualLocation);

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-module-project-wizard-location.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newModule.location().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
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
			SOY_PORTLET, SPRING_MVC_PORTLET, TEMPLATE_CONTEXT_CONCONTRIBUTOR, THEME, THEME_CONTRIBUTOR, WAR_HOOK,
			WAR_MVC_PORTLET
		};

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		Assert.assertEquals(MVC_PORTLET, wizardAction.newModule.projectTemplateName().getText());
		Assert.assertArrayEquals(templates, wizardAction.newModule.projectTemplateName().items());

		wizardAction.cancel();
	}

	@Test
	public void validateProjectName() {
		wizardAction.openNewLiferayModuleWizard();

		Assert.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));
		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-liferay-module-project-wizard-project-name.csv"))) {

			wizardAction.newModule.projectName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
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

		Assert.assertEquals(NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.deleteBtn().click();

		wizardAction.newModuleInfo.prepareProperties(StringPool.BLANK, projectName);

		Assert.assertEquals(NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.deleteBtn().click();

		wizardAction.newModuleInfo.prepareProperties(projectName, StringPool.BLANK);

		Assert.assertEquals(VALUE_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(2));

		wizardAction.newModuleInfo.deleteBtn().click();

		wizardAction.newModuleInfo.prepareProperties(projectName, projectName);

		Assert.assertEquals(CONFIGURE_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void validateSecondPageInitialState() {
		String projectName = "test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(projectName);

		wizardAction.next();

		Assert.assertEquals(CONFIGURE_COMPONENT_CLASS, wizardAction.getValidationMsg(2));
		Assert.assertEquals(StringPool.BLANK, wizardAction.newModuleInfo.componentClassName().getText());
		Assert.assertEquals(StringPool.BLANK, wizardAction.newModuleInfo.packageName().getText());

		wizardAction.cancel();
	}

}