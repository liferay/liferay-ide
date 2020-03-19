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

package com.liferay.ide.functional.module.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle71Support;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class NewModuleProjectWizardLiferayWorkspaceGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle71Support liferayWorkspace = new LiferayWorkspaceGradle71Support(bot);

	@Test
	public void createActivator() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), ACTIVATOR);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createApi() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), API);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createContentTargetingReport() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), CONTENT_TARGETING_REPORT);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createContentTargetingRule() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), CONTENT_TARGETING_RULE);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createContentTargetingTrackingAction() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createControlMenuEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), CONTROL_MENU_ENTRY);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createFormField() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), FORM_FIELD);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createPanelApp() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PANEL_APP);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createPortletConfigurationIcon() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PORTLET_CONFIGURATION_ICON);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createPortletProvider() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PORTLET_PROVIDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createPortletToolbarContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createRest() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), REST);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createService() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SERVICE);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SERVICE_WRAPPER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createSimulationPanelEntry() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SIMULATION_PANEL_ENTRY);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createTemplateContextContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createThemeContributor() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), THEME_CONTRIBUTOR);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createWarHook() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), WAR_HOOK);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getWarFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getWarFiles(project.getName()));
	}

	@Test
	public void createWarMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getWarFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getWarFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}