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

package com.liferay.ide.ui.project.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class NewLiferayModuleProjectWizardTests extends SwtbotBase {

	@Test
	public void createActivatorModuleProject() {
		String projectName = "test-activator-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, ACTIVATOR);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createApiModuleProject() {
		String projectName = "test-api-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, API);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createContentTargetingReportModuleProject() {
		String projectName = "test-content-targeting-report-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTENT_TARGETING_REPORT);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createContentTargetingRuleModuleProject() {
		String projectName = "test-content-targeting-rule-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTENT_TARGETING_RULE);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createContentTargetingTrackingActionModuleProject() {
		String projectName = "test-content-targeting-tracking-action-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createControlMenuEntryModuleProject() {
		String projectName = "test-control-menu-entry-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, CONTROL_MENU_ENTRY);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createFormFieldModuleProject() {
		String projectName = "test-form-field-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, FORM_FIELD);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMvcportletModuleProject() {
		String projectName = "test-mvc-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createMvcPortletModuleProjectInLiferayWorkspace() throws IOException {
		wizardAction.openNewLiferayWorkspaceWizard();

		String liferayWorkspaceName = "liferayWorkspace";

		wizardAction.prepareLiferayWorkspaceGradle(liferayWorkspaceName);

		wizardAction.finishToWait();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-mvc-portlet-in_lws";

		wizardAction.prepareLiferayModuleGradle(projectName, MVC_PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(liferayWorkspaceName);
	}

	@Test
	public void createPanelAppModuleProject() {
		String projectName = "test-panel-app-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PANEL_APP);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletConfigurationIconModuleProject() {
		String projectName = "test-portlet-configuration-icon-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET_CONFIGURATION_ICON);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletModuleProject() {
		String projectName = "test-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletProviderModuleProject() {
		String projectName = "test-portlet-provider-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET_PROVIDER);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createPortletToolbarContributorModuleProject() {
		String projectName = "test-portlet-toolbar-contributor-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createRestModuleProject() {
		String projectName = "test-rest-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, REST);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createServiceBuilderModuleProject() {
		String projectName = "test-service-builder-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE_BUILDER);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createServiceModuleProject() {
		String projectName = "test-service-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE);

		wizardAction.next();

		wizardAction.openSelectServiceDialog();

		dialogAction.prepareText("*lifecycleAction");

		dialogAction.confirm();

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createServiceWrapperModuleProject() {
		String projectName = "test-service-wrapper-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE_WRAPPER);

		wizardAction.next();

		wizardAction.openSelectServiceDialog();

		dialogAction.prepareText("*BookmarksEntryLocalServiceWrapper");

		dialogAction.confirm();

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createSimulationPanelEntryModuleProject() {
		String projectName = "test-simulation-panel-entry-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SIMULATION_PANEL_ENTRY);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createTemplateContextContributorModuleProject() {
		String projectName = "test-template-context-contributor-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createThemeContributor() {
		String projectName = "test-theme-contributor-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, THEME_CONTRIBUTOR);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createThemeModuleProject() {
		String projectName = "test-theme-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, THEME);

		wizardAction.finishToWait();

		viewAction.deleteProject(projectName);
	}

}