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

package com.liferay.ide.ui.module.deploy.tests.base;

import com.liferay.ide.ui.liferay.ServerTestBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;

import org.junit.Rule;

/**
 * @author Terry Jia
 * @author Lily Li
 */
public class DeployModuleGradleTomcat7xBase extends ServerTestBase {

	public void deployActivator() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), ACTIVATOR);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployApi() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), API);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployControlMenuEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), CONTROL_MENU_ENTRY);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployFormField() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), FORM_FIELD);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployPanelApp() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PANEL_APP);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployPortletConfigurationIcon() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET_CONFIGURATION_ICON);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployPortletProvider() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET_PROVIDER);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployPortletToolbarContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployRest() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), REST);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployService() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE);

		wizardAction.next();

		wizardAction.newModuleInfo.openSelectServiceDialog();

		dialogAction.prepareText("*lifecycleAction");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE_WRAPPER);

		wizardAction.next();

		wizardAction.newModuleInfo.openSelectServiceDialog();

		dialogAction.prepareText("*BookmarksEntryLocalServiceWrapper");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deploySimulationPanelEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SIMULATION_PANEL_ENTRY);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployTemplateContextContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployThemeContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), THEME_CONTRIBUTOR);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployWarHook() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_HOOK);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployWarMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}