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

package com.liferay.ide.functional.portlet.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle71Support;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rui Wang
 */
public class NewPortletModuleLiferayWorkspaceGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle71Support liferayWorkspace = new LiferayWorkspaceGradle71Support(bot);

	@Test
	public void createFreemarkerPortlet() {
		String packageName = "test.freemarker.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), FREEMARKER_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createMvcPortlet() {
		String packageName = "test.mvc.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		jobAction.waitForValidate(project.getName());

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmAngularPortlet() {
		String packageName = "test.npm.angular.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), NPM_ANGULAR_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmReactPortlet() {
		String packageName = "test.npm.react.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), NPM_REACT_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createNpmVuejsPortlet() {
		String packageName = "test.npm.vuejs.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), NPM_VUEJS_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Ignore("ignore because blade 3.7 remove portlet template")
	@Test
	public void createPortlet() {
		String packageName = "test.npm.angular.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Ignore("ignore because blade 3.7 remove soy-portlet template")
	@Test
	public void createSoyPortlet() {
		String packageName = "test.soy.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SOY_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getName(), "modules", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Ignore("ignore to wait new Spring MVC Portlet wizard")
	@Test
	public void createSpringMvcPortlet() {
		String packageName = "test.spring.mvc.portlet.gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SPRING_MVC_PORTLET);

		wizardAction.next();

		wizardAction.newModuleInfo.preparePackageName(packageName);

		wizardAction.finish();

		String[] projectNames = {liferayWorkspace.getName(), "wars", project.getName()};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}