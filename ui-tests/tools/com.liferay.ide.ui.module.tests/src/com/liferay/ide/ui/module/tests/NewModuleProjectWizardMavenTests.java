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
 * @author Sunny Shi
 * @author Ying Xu
 */
public class NewModuleProjectWizardMavenTests extends SwtbotBase {

	@Test
	public void createActivator() {
		String projectName = "test-activator-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, ACTIVATOR);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createApi() {
		String projectName = "test-api-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, API);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createContentTargetingReport() {
		String projectName = "test-content-targeting-report-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, CONTENT_TARGETING_REPORT);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createContentTargetingRule() {
		String projectName = "test-content-targeting-rule-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, CONTENT_TARGETING_RULE);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createContentTargetingTrackingAction() {
		String projectName = "test-content-targeting-tracking-action-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createControlMenuEntry() {
		String projectName = "test-control-menu-entry-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, CONTROL_MENU_ENTRY);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createFormField() {
		String projectName = "test-form-field-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, FORM_FIELD);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMvcPortlet() {
		String projectName = "test-mvc-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPanelApp() {
		String projectName = "test-panel-app-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, PANEL_APP);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortlet() {
		String projectName = "test-portlet-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, PORTLET);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletConfigurationIcon() {
		String projectName = "test-portlet-configuration-icon-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, PORTLET_CONFIGURATION_ICON);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletProvider() {
		String projectName = "test-portlet-provider-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, PORTLET_PROVIDER);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletToolbarContributor() {
		String projectName = "test-portlet-toolbar-contributor-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createRest() {
		String projectName = "test-rest-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, REST);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createService() {
		String projectName = "test-service-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SERVICE);

		wizardAction.next();

		wizardAction.openSelectServiceDialog();

		dialogAction.prepareText("*lifecycleAction");

		dialogAction.confirm();

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createServiceBuilder() {
		String projectName = "test-service-builder-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SERVICE_BUILDER);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createServiceWrapper() {
		String projectName = "test-service-wrapper-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SERVICE_WRAPPER);

		wizardAction.next();

		wizardAction.openSelectServiceDialog();

		dialogAction.prepareText("*bookmarksEntryServiceWrapper");

		dialogAction.confirm();

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createSimulationPanelEntry() {
		String projectName = "test-simulation-panel-entry-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SIMULATION_PANEL_ENTRY);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createTemplateContextContributor() {
		String projectName = "test-template-context-contributor-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Ignore
	@Test
	public void createTheme() {
		String projectName = "test-theme-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, THEME);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createThemeContributor() {
		String projectName = "test-theme-contributor-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, THEME_CONTRIBUTOR);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		viewAction.deleteProject(projectName);
	}

}