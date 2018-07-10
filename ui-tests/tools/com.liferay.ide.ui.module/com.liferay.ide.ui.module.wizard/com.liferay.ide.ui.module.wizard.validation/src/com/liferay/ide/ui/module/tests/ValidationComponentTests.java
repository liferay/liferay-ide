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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Lily Li
 * @author Ying Xu
 */
public class ValidationComponentTests extends SwtbotBase {

	@Test
	public void checkBrowsePackageNameDialog() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText("*packagename");

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.prepareText(project.getName());

		dialogAction.selectTableItem(project.getName());

		dialogAction.selectTableItem(project.getName() + ".constants");

		dialogAction.selectTableItem(project.getName()+ ".portlet");

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkComponentClassName() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-component-wizard-class-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newLiferayComponent.componentClassName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkComponentClassTemplate() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] templates = {
			AUTH_FAILURES, AUTH_MAX_FAILURE, AUTHENTICATOR, FRIENDLY_URL_MAPPER, GOGO_COMMAND, INDEXER_POST_PROCESSOR,
			LOGIN_PRE_ACTION, MVC_PORTLET_UPCASE, MODEL_LISTENER, POLLER_PROCESSOR, PORTLET_UPCASE,
			PORTLET_ACTION_COMMAND, PORTLET_FILTER, REST_UPCASE, SERVICE_WRAPPER_UPCASE, STRUTS_IN_ACTION,
			STRUTS_PORTLET_ACTION
		};

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertArrayEquals(templates, wizardAction.newLiferayComponent.componentClassTemplate().items());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkExistingComponentClass() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String packageName = project.getName() + ".portlet";
		String className = project.getName().substring(0, 1).toUpperCase() + project.getName().substring(1);

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.packageName().setText(packageName);

		Assert.assertEquals(packageName + "." + className + "Portlet" + ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		wizardAction.newLiferayComponent.componentClassName().setText(project.getName() + "portlet");

		// wait for IDE-4059 fixed
		// Assert.assertEquals(packageName + "." + className + "Portlet" + ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkInfoInitialState() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertEquals(CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		Assert.assertEquals(project.getName(), wizardAction.newLiferayComponent.projectName().getText());

		Assert.assertTrue(wizardAction.newLiferayComponent.browsePackageBtn().isEnabled());

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertEquals(NO_SUITABLE_LIFERAY_MODULE_PROJECT, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, wizardAction.newLiferayComponent.projectName().getText());

		Assert.assertEquals(StringPool.BLANK, wizardAction.newLiferayComponent.packageName().getText());

		Assert.assertEquals(StringPool.BLANK, wizardAction.newLiferayComponent.componentClassName().getText());

		Assert.assertEquals(PORTLET_UPCASE, wizardAction.newLiferayComponent.componentClassTemplate().getText());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkModelListenerTemplate() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String template = MODEL_LISTENER;

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(template);

		Assert.assertEquals(MODEL_CLASS_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(3));

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.prepareModelClass("modelClass");

		Assert.assertEquals("\"modelClass\"" + IS_NOT_AMONG_POSSIBLE_VALUES, wizardAction.getValidationMsg(3));

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*modelClass");

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.prepareText("*blogs");

		dialogAction.selectTableItem("com.liferay.blogs.kernel.model.BlogsEntry");

		dialogAction.selectTableItem("com.liferay.blogs.kernel.model.BlogsStatsUser");

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkPackageName() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-component-wizard-package-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newLiferayComponent.packageName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkProjectName() {
		String projectName1 = "mvc-portlet-test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName1);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String projectName2 = "portlet-test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName2, PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {projectName1, projectName2};

		String packageName = "portlet.test.portlet";

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertArrayEquals(projectNames, wizardAction.newLiferayComponent.projectName().items());

		wizardAction.newLiferayComponent.prepare(projectName2, packageName);

		Assert.assertEquals(
			"portlet.test.portlet.PortletTestPortlet" + ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName1);

		viewAction.project.closeAndDelete(projectName2);
	}

	@Ignore("ignore to wait target platform way")
	@Test
	public void checkServiceWrapperTemplate() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(template);

		Assert.assertEquals(SERVICE_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(3));

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.prepareServiceName("serviceName");

		Assert.assertEquals(CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, wizardAction.getValidationMsg(3));

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*serviceName");

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.prepareText("*microblogs");

		dialogAction.selectTableItem("com.liferay.microblogs.service.MicroblogsEntryLocalServiceWrapper");

		dialogAction.selectTableItem("com.liferay.microblogs.service.MicroblogsEntryServiceWrapper");

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}