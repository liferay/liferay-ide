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
import com.liferay.ide.ui.liferay.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.ui.liferay.util.ValidationMsg;
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ValidationComponentTests extends SwtbotBase {

	@Test
	public void checkBrowsePackageNameDialog() {
		String projectName = "test-browse-dialog";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText("*packagename");

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.prepareText("*test.browse.dialog");

		dialogAction.selectTableItem("test.browse.dialog");

		dialogAction.selectTableItem("test.browse.dialog.constants");

		dialogAction.selectTableItem("test.browse.dialog.portlet");

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Ignore("wait for IDE-3585 fixed")
	@Test
	public void checkComponentClassName() {
		String projectName = "test-myclass-name";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-component-wizard-class-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			_newComponentWizard.getComponentClassName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkComponentClassTemplate() {
		String projectName = "test-template";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		String[] templates = {
			AUTH_FAILURES, AUTH_MAX_FAILURE, AUTHENTICATOR, FRIENDLY_URL_MAPPER, GOGO_COMMAND, INDEXER_POST_PROCESSOR,
			LOGIN_PRE_ACTION, MVC_PORTLET_UPCASE, MODEL_LISTENER, POLLER_PROCESSOR, PORTLET_UPCASE,
			PORTLET_ACTION_COMMAND, PORTLET_FILTER, REST_UPCASE, SERVICE_WRAPPER_UPCASE, STRUTS_IN_ACTION,
			STRUTS_PORTLET_ACTION
		};

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertArrayEquals(templates, _newComponentWizard.getComponentClassTemplates().items());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkInfoInitialState() {
		String projectName = "test-component-info-initial-state";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertEquals(CREATE_A_NEW_LIEFRAY_COMPONENT_CLASS, wizardAction.getValidationMsg(2));

		Assert.assertEquals(projectName, _newComponentWizard.getProjectNames().getText());

		Assert.assertTrue(_newComponentWizard.getPackageBrowseBtn().isEnabled());

		Assert.assertTrue(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewLiferayComponentClassWizard();

		//Assert.assertEquals(NO_SUITABLE_LIFERAY_MODULE_PROJECT, wizardAction.getValidationMsg(2));

		Assert.assertEquals(StringPool.BLANK, _newComponentWizard.getProjectNames().getText());

		Assert.assertEquals(StringPool.BLANK, _newComponentWizard.getPackageName().getText());

		Assert.assertEquals(StringPool.BLANK, _newComponentWizard.getComponentClassName().getText());

		Assert.assertEquals(PORTLET_UPCASE, _newComponentWizard.getComponentClassTemplates().getText());

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();
	}

	@Test
	public void checkModelListenerTemplate() {
		String projectName = "test-model-listener-template";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		String template = MODEL_LISTENER;

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(template);

		// wait for IDE-3585 fixed
		// Assert.assertEquals(MODEL_CLASS_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(3));

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

		viewAction.project.closeAndDelete(projectName);
	}

	@Ignore("wait for IDE-3585 fixed")
	@Test
	public void checkPackageName() {
		String projectName = "test-mypackage-name";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-component-wizard-package-name.csv"))) {

			if (!msg.getOs().equals(Platform.getOS())) {
				continue;
			}

			_newComponentWizard.getPackageName().setText(msg.getInput());

			Assert.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void checkProjectName() {
		String projectName1 = "mvc-portlet-test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName1);

		wizardAction.finish();

		String projectName2 = "portlet-test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName2);

		wizardAction.finish();

		String[] projectNames = {projectName1, projectName2};

		String packageName = "portlet.test.portlet";

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertArrayEquals(projectNames, _newComponentWizard.getProjectNames().items());

		wizardAction.newLiferayComponent.prepare(projectName2, packageName);

		// wait for IDE-3585 fixed
		// Assert.assertEquals(
		// "portlet.test.portlet.PortletTestPortlet" + ALREADY_EXISTS, wizardAction.getValidationMsg(2));

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName1);

		viewAction.project.closeAndDelete(projectName2);
	}

	@Ignore("ignore to wait target platform way")
	@Test
	public void checkServiceWrapperTemplate() {
		String projectName = "test-service-wrapper-template";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(template);

		// wait for IDE-3585 fixed
		// Assert.assertEquals(SERVICE_NAME_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(3));

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.prepareServiceName("serviceName");

		// wait for IDE-3599 fixed
		// Assert.assertEquals("\"serviceName\"" + IS_NOT_AMONG_POSSIBLE_VALUES, wizardAction.getValidationMsg(3));

		// Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*serviceName");

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.prepareText("*microblogs");

		dialogAction.selectTableItem("com.liferay.microblogs.service.MicroblogsEntryLocalServiceWrapper");

		dialogAction.selectTableItem("com.liferay.microblogs.service.MicroblogsEntryServiceWrapper");

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(projectName);
	}

	private static final NewLiferayComponentWizard _newComponentWizard = new NewLiferayComponentWizard(bot);

}