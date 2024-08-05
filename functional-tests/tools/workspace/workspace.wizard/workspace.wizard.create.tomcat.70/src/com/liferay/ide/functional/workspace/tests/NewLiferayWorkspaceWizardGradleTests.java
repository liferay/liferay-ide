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

package com.liferay.ide.functional.workspace.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 * @author Terry Jia
 * @author Lily Li
 * @author Rui Wang
 */
public class NewLiferayWorkspaceWizardGradleTests extends SwtbotBase {

	String expectedBuildGradle =
			"dependencies {\n"+
		"	compileOnly group: \"org.osgi\", name: \"org.osgi.core\"\n"+
		"	compileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\"\n"+
	"}";

	@Test
	public void checkDependenciesVersionForExt() {
		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "portal-7.1-ga4");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModulesExtWizard();

		String projectName = "test-ext";

		wizardAction.newModulesExt.prepare(projectName);

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "ext", projectName, "build.gradle");

		validationAction.assertDoesNotContains("version", editorAction.getContent());

		editorAction.close();

		String[] projectNames = {project.getName(), "ext", projectName};
		String[] newModuleNames = {project.getName(), "ext"};

		viewAction.project.closeAndDeleteFromDisk(projectNames);
		viewAction.project.closeAndDeleteFromDisk(newModuleNames);
		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Ignore ("IDE-4823 After building the wars project, it will also be placed in the gradle workspace module folder")
	@Test
	public void createLiferayWorkspaceChangeWarsDir() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "gradle.properties");

		StringBuffer sb = new StringBuffer();

		String newWarsFolderName = "warsTest";

		sb.append("liferay.workspace.wars.dir");
		sb.append("=");
		sb.append(newWarsFolderName);

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-theme";

		wizardAction.newModule.prepareGradleInWorkspace("test-theme", THEME);

		wizardAction.finish();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningJobs();

		String[] projectNames = {project.getName(), newWarsFolderName, projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDelete(projectNames);

		String[] newModuleNames = {project.getName(), newWarsFolderName};

		viewAction.project.closeAndDelete(newModuleNames);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkDependenciesVersionForModule() {
		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "portal-7.2-ga2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-api";

		wizardAction.newModule.prepareGradleInWorkspace(projectName, API);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "modules", projectName, "build.gradle");

		validationAction.assertDoesNotContains("version", editorAction.getContent());

		editorAction.close();

		String[] projectNames = {project.getName(), "modules", projectName};
		String[] newModuleNames = {project.getName(), "modules"};

		viewAction.project.closeAndDeleteFromDisk(projectNames);
		viewAction.project.closeAndDeleteFromDisk(newModuleNames);
		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void checkDependenciesVersionForWar() {
		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(),"portal-7.2-ga2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-war-hook";

		wizardAction.newModule.prepareGradleInWorkspace(projectName, WAR_HOOK);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "modules", projectName, "build.gradle");

		validationAction.assertDoesNotContains("version", editorAction.getContent());

		editorAction.close();

		String[] projectNames = {project.getName(), "modules", projectName};
		String[] newModuleNames = {project.getName(), "modules"};

		viewAction.project.closeAndDeleteFromDisk(projectNames);
		viewAction.project.closeAndDeleteFromDisk(newModuleNames);
		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void createLiferayWorkspace70() {
		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "portal-7.0-ga7");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		String productKey = "liferay.workspace.product = portal-7.0-ga7";

		viewAction.project.openFile(project.getName(), "gradle.properties");

		validationAction.assertContains(productKey, editorAction.getContent());

		editorAction.close();

		String[] moduleNames = {project.getName(), "modules"};

		Assert.assertTrue(viewAction.project.visibleFileTry(moduleNames));

		String[] themeNames = {project.getName(), "themes"};

		Assert.assertTrue(viewAction.project.visibleFileTry(themeNames));

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void createLiferayWorkspace71() {
		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "portal-7.1-ga4");

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.deselectDownloadLiferayBundle();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		String productKey = "liferay.workspace.product = portal-7.1-ga4";

		viewAction.project.openFile(project.getName(), "gradle.properties");

		validationAction.assertContains(productKey, editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void createLiferayWorkspaceChangeLocation() {
		String workspacePath = envAction.getEclipseWorkspacePathOSString();

		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.deselectUseDefaultLocation();

		String newFolderName = "changeLocation";

		wizardAction.newLiferayWorkspace.prepareLocation(workspacePath + "/" + newFolderName);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Test
	public void createLiferayWorkspaceChangeModulesDir() {
		wizardAction.openNewLiferayWorkspaceWizard();

		jobAction.waitForNoRunningJobs();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "portal-7.2-ga2");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), "gradle.properties");

		String gradlePropertiesFile = editorAction.getContent();

		String previousText = "liferay.workspace.modules.dir = modules";
		String newText = "liferay.workspace.modules.dir = modulesTest";

		editorAction.selectText("liferay.workspace.modules.dir = modules");

		editorAction.setText(gradlePropertiesFile.replace(previousText, newText));
	
		editorAction.save();

		editorAction.close();

		wizardAction.openNewLiferayModuleWizard();

		String projectName = "test-mvc-portlet";

		wizardAction.newModule.prepareGradleInWorkspace(projectName, MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(project.getName());

		jobAction.waitForNoRunningJobs();

		String[] projectNames = {project.getName(), "modulesTest", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);

		String[] newModuleNames = {project.getName(), "modulesTest"};

		viewAction.project.closeAndDeleteFromDisk(newModuleNames);

		viewAction.project.closeAndDeleteFromDisk(project.getName());
	}

	@Ignore("Ignore forever and test the download bundle in createLiferayWorkspaceWithDownloadBundleChangeBundleUrl")
	@Test
	public void createLiferayWorkspaceWithDownloadBundle() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName());

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-download-bundle");

		dialogAction.preferences.confirm();
	}

	@Ignore("IDE-4827 gradle workspace wizard no support modfiy bundle url,maven workspace file has related tests")
	@Test
	public void createLiferayWorkspaceWithDownloadBundleChangeBundleUrl() {
		if (!envAction.internal()) {
			return;
		}

		// Use the internal server instead of the public server and also need to append the internal host

		String bundleUrl =
			"https://releases-cdn.liferay.com/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(project.getName(), "7.0");

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		wizardAction.newLiferayWorkspace.setServerName("Liferay 7-change-bundle-url");

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleUrl);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay 7-change-bundle-url");

		dialogAction.preferences.confirm();

		viewAction.project.runGradleInitBundle(project.getName());

		jobAction.waitForNoRunningJobs();
		jobAction.waitForConsoleContent("[Gradle Operations]", "BUILD SUCCESSFUL", M1);

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "bundles"));

		Assert.assertTrue(viewAction.servers.visibleServer(LIFERAY_PORTAL_BUNDLE));

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(LIFERAY_PORTAL_BUNDLE);

		dialogAction.preferences.confirm();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}