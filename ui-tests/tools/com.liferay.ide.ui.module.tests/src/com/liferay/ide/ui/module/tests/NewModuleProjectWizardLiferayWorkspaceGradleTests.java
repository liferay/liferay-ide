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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class NewModuleProjectWizardLiferayWorkspaceGradleTests extends SwtbotBase {

	@AfterClass
	public static void cleanLiferayWorkspace() {
		viewAction.deleteProject(_liferayWorkspaceName);
	}

	@BeforeClass
	public static void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(_liferayWorkspaceName);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(_liferayWorkspaceName));
	}

	@Test
	public void createActivator() {
		String projectName = "test-activator-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, ACTIVATOR);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createApi() {
		String projectName = "test-api-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, API);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createContentTargetingReport() {
		String projectName = "test-content-targeting-report-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTENT_TARGETING_REPORT);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createContentTargetingRule() {
		String projectName = "test-content-targeting-rule-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTENT_TARGETING_RULE);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createContentTargetingTrackingAction() {
		String projectName = "test-content-targeting-tracking-action-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createControlMenuEntry() {
		String projectName = "test-control-menu-entry-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTROL_MENU_ENTRY);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createFormField() {
		String projectName = "test-form-field-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, FORM_FIELD);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createMvcPortlet() {
		String projectName = "test-mvc-portlet-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createPanelApp() {
		String projectName = "test-panel-app-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PANEL_APP);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createPortlet() {
		String projectName = "test-portlet-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createPortletConfigurationIcon() {
		String projectName = "test-portlet-configuration-icon-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET_CONFIGURATION_ICON);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createPortletProvider() {
		String projectName = "test-portlet-provider-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET_PROVIDER);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createPortletToolbarContributor() {
		String projectName = "test-portlet-toolbar-contributor-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createRest() {
		String projectName = "test-rest-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, REST);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createService() {
		String projectName = "test-service-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createServiceBuilder() {
		String projectName = "test-service-builder-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE_BUILDER);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createServiceWrapper() {
		String projectName = "test-service-wrapper-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE_WRAPPER);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createSimulationPanelEntry() {
		String projectName = "test-simulation-panel-entry-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SIMULATION_PANEL_ENTRY);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createTemplateContextContributor() {
		String projectName = "test-template-context-contributor-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createTheme() {
		String projectName = "test-theme-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, THEME);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "wars", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	@Test
	public void createThemeContributor() {
		String projectName = "test-theme-contributor-in-lws";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, THEME_CONTRIBUTOR);

		wizardAction.finishToWait();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.deleteProject(projectNames);
	}

	private static final String _liferayWorkspaceName = "test-liferay-workspace-gradle";

}