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

package com.liferay.ide.functional.module.deploy.base;

import com.liferay.ide.functional.liferay.ServerTestBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceSupport;

import org.junit.Rule;

/**
 * @author Lily Li
 */
public abstract class DeployModuleLiferayWorkspaceGradleTomcat7xBase extends ServerTestBase {

	public void deployApi() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), API);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployControlMenuEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), CONTROL_MENU_ENTRY);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployFormField() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), FORM_FIELD);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployPanelApp() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PANEL_APP);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		if (getVersion().equals("7.0")) {
			jobAction.waitForNoRunningJobs();

			viewAction.project.openFile(getLiferayWorkspace().getName(), "modules", project.getName(), "build.gradle");

			String text = "configurations.all {resolutionStrategy.force 'javax.portlet:portlet-api:2.0'}";

			editorAction.setText(editorAction.getContent() + "\n" + text);

			editorAction.save();

			jobAction.waitForNoRunningJobs();

			editorAction.close();
		}

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployPortletConfigurationIcon() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PORTLET_CONFIGURATION_ICON);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		if (getVersion().equals("7.0")) {
			jobAction.waitForNoRunningJobs();

			viewAction.project.openFile(getLiferayWorkspace().getName(), "modules", project.getName(), "build.gradle");

			String text = "configurations.all {resolutionStrategy.force 'javax.portlet:portlet-api:2.0'}";

			editorAction.setText(editorAction.getContent() + "\n" + text);

			editorAction.save();

			jobAction.waitForNoRunningJobs();

			editorAction.close();
		}

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployPortletProvider() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PORTLET_PROVIDER);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		if (getVersion().equals("7.0")) {
			jobAction.waitForNoRunningJobs();

			viewAction.project.openFile(getLiferayWorkspace().getName(), "modules", project.getName(), "build.gradle");

			String text = "configurations.all {resolutionStrategy.force 'javax.portlet:portlet-api:2.0'}";

			editorAction.setText(editorAction.getContent() + "\n" + text);

			editorAction.save();

			jobAction.waitForNoRunningJobs();

			editorAction.close();
		}

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployPortletToolbarContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		if (getVersion().equals("7.0")) {
			jobAction.waitForNoRunningJobs();

			viewAction.project.openFile(getLiferayWorkspace().getName(), "modules", project.getName(), "build.gradle");

			String text = "configurations.all {resolutionStrategy.force 'javax.portlet:portlet-api:2.0'}";

			editorAction.setText(editorAction.getContent() + "\n" + text);

			editorAction.save();

			jobAction.waitForNoRunningJobs();

			editorAction.close();
		}

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployRest() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), REST);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		if (getVersion().equals("7.0")) {
			jobAction.waitForNoRunningJobs();

			viewAction.project.openFile(getLiferayWorkspace().getName(), "modules", project.getName(), "build.gradle");

			String text =
				"configurations.all {resolutionStrategy.force 'javax.portlet:portlet-api:2.0'}\n" +
					"configurations.all {resolutionStrategy.force 'javax.ws.rs:javax.ws.rs-api:2.0'}";

			editorAction.setText(editorAction.getContent() + "\n" + text);

			editorAction.save();

			jobAction.waitForNoRunningJobs();

			editorAction.close();
		}

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployService() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SERVICE);

		wizardAction.next();

		wizardAction.newModuleInfo.openSelectServiceDialog();

		dialogAction.prepareText("*lifecycleAction");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.openFile(
			getLiferayWorkspace().getName(), "modules", project.getName(), "src/main/java", project.getName(),
			project.getCapitalName() + ".java");

		viewAction.project.implementMethods(
			getLiferayWorkspace().getName(), "modules", project.getName(), "src/main/java", project.getName(),
			project.getCapitalName() + ".java");

		dialogAction.confirm();

		editorAction.save();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SERVICE_WRAPPER);

		wizardAction.next();

		wizardAction.newModuleInfo.openSelectServiceDialog();

		dialogAction.prepareText("*AccountLocalServiceWrapper");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deploySimulationPanelEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SIMULATION_PANEL_ENTRY);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		if (getVersion().equals("7.0")) {
			jobAction.waitForNoRunningJobs();

			viewAction.project.openFile(getLiferayWorkspace().getName(), "modules", project.getName(), "build.gradle");

			String text = "configurations.all {resolutionStrategy.force 'javax.portlet:portlet-api:2.0'}";

			editorAction.setText(editorAction.getContent() + "\n" + text);

			editorAction.save();

			jobAction.waitForNoRunningJobs();

			editorAction.close();
		}

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployTemplateContextContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployThemeContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), THEME_CONTRIBUTOR);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployWarHook() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), WAR_HOOK);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M5);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployWarMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M5);

		viewAction.servers.removeModule(getServerName(), project.getName());

		dialogAction.confirm();

		jobAction.waitForNoRunningJobs();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	protected abstract LiferayWorkspaceSupport getLiferayWorkspace();

	protected abstract String getServerName();

	protected abstract String getStartedLabel();

	protected abstract String getVersion();

}