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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class NewModuleProjectWizardGradleTests extends SwtbotBase {

	@Test
	public void createActivator() {
		String projectName = "test-activator-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, ACTIVATOR);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createApi() {
		String projectName = "test-api-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, API);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createContentTargetingReport() {
		String projectName = "test-content-targeting-report-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, CONTENT_TARGETING_REPORT);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createContentTargetingRule() {
		String projectName = "test-content-targeting-rule-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, CONTENT_TARGETING_RULE);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createContentTargetingTrackingAction() {
		String projectName = "test-content-targeting-tracking-action-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createControlMenuEntry() {
		String projectName = "test-control-menu-entry-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, CONTROL_MENU_ENTRY);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createFormField() {
		String projectName = "test-form-field-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, FORM_FIELD);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createPanelApp() {
		String projectName = "test-panel-app-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, PANEL_APP);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createPortletConfigurationIcon() {
		String projectName = "test-portlet-configuration-icon-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, PORTLET_CONFIGURATION_ICON);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createPortletProvider() {
		String projectName = "test-portlet-provider-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, PORTLET_PROVIDER);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createPortletToolbarContributor() {
		String projectName = "test-portlet-toolbar-contributor-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createRest() {
		String projectName = "test-rest-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, REST);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Ignore("ignore to wait target platform way")
	@Test
	public void createService() {
		String projectName = "test-service-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SERVICE);

		wizardAction.next();

		wizardAction.newModuleInfo.openSelectServiceDialog();

		dialogAction.prepareText("*lifecycleAction");

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Ignore("ignore to wait target platform way")
	@Test
	public void createServiceWrapper() {
		String projectName = "test-service-wrapper-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SERVICE_WRAPPER);

		wizardAction.next();

		wizardAction.newModuleInfo.openSelectServiceDialog();

		dialogAction.prepareText("*BookmarksEntryLocalServiceWrapper");

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createSimulationPanelEntry() {
		String projectName = "test-simulation-panel-entry-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SIMULATION_PANEL_ENTRY);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createTemplateContextContributor() {
		String projectName = "test-template-context-contributor-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createThemeContributor() {
		String projectName = "test-theme-contributor-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, THEME_CONTRIBUTOR);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createWarHook() {
		String projectName = "test-war-hook-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, WAR_HOOK);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createWarMvcPortlet() {
		String projectName = "test-war-mvc-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, WAR_MVC_PORTLET);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName));

		viewAction.project.closeAndDelete(projectName);
	}

}