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
import com.liferay.ide.ui.liferay.base.LiferayWorkspaceMavenSupport;
import com.liferay.ide.ui.liferay.base.TimestampSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class NewModuleProjectWizardLiferayWorkspaceMavenTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceMavenSupport liferayWorkspace = new LiferayWorkspaceMavenSupport(bot);

	@Test
	public void createActivator() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, ACTIVATOR);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createApi() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, API);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createContentTargetingReport() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, CONTENT_TARGETING_REPORT);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createContentTargetingRule() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, CONTENT_TARGETING_RULE);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createContentTargetingTrackingAction() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, CONTENT_TARGETING_TRACKING_ACTION);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createControlMenuEntry() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, CONTROL_MENU_ENTRY);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFormField() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, FORM_FIELD);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPanelApp() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, PANEL_APP);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortletConfigurationIcon() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, PORTLET_CONFIGURATION_ICON);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortletProvider() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, PORTLET_PROVIDER);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createPortletToolbarContributor() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, PORTLET_TOOLBAR_CONTRIBUTOR);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createRest() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, REST);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createService() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, SERVICE);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createServiceWrapper() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, SERVICE_WRAPPER);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createSimulationPanelEntry() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, SIMULATION_PANEL_ENTRY);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createTemplateContextContributor() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createThemeContributor() {
		String projectName = timestamp.getName();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, THEME_CONTRIBUTOR);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getModulesDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createWarHook() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, WAR_HOOK);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getWarsDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createWarMvcPortlet() {
		String projectName = timestamp.getName();

		String packageName = "com.liferay.ide.test";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, WAR_MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.prepare(packageName);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(liferayWorkspace.getLiferayWorkspaceName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		String[] projectNames =
			{liferayWorkspace.getLiferayWorkspaceName(), liferayWorkspace.getWarsDirName(), projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Rule
	public TimestampSupport timestamp = new TimestampSupport(bot);

}