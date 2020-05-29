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
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle72Support;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Rui Wang
 */
public class NewComponentWizardGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle72Support liferayWorkspace = new LiferayWorkspaceGradle72Support(bot);

	@Test
	public void createComponentModelListener() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyListener";
		String packageName = "com.liferay.ide.test";
		String template = MODEL_LISTENER;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*com.liferay.blogs.kernel.model.BlogsEntry");

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", packageName,
				className + ".java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentOnMultipleProject() {
		String firstProjectName = "first-component-gradle";
		String secondProjectName = "second-component-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(firstProjectName, MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(secondProjectName, MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", firstProjectName, "src/main/java", "content",
				"FirstComponentGradlePortlet.java"));

		editorAction.close();

		wizardAction.openNewLiferayComponentClassWizard();

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.newLiferayComponent.prepareProjectName(secondProjectName);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", secondProjectName, "src/main/java", "content",
				"SecondComponentGradlePortlet.java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(firstProjectName));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(secondProjectName));
	}

	@Test
	public void createComponentPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyPortlet";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", packageName,
				className + ".java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyServiceWrapper";
		String packageName = "com.liferay.ide.test";
		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*bookmarksEntryLocal");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", packageName,
				className + ".java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentShortcuts() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openFileMenuLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(MVC_PORTLET_UPCASE);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", "content",
				project.getCapitalName() + "MVCPortlet.java"));

		editorAction.close();

		viewAction.project.openComponentClassWizard(liferayWorkspace.getName(), "modules", project.getName());

		wizardAction.newLiferayComponent.prepare(REST_UPCASE);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", "content",
				project.getCapitalName() + "RestService.java"));

		editorAction.close();

		wizardAction.openNewBtnLiferayComponentClassWizard();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", "content",
				project.getCapitalName() + "Portlet.java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentWithPackage() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String packageName = project.getName() + ".constants";

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText(packageName);

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", packageName,
				project.getCapitalName() + "Portlet.java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createDefaultComponent() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "modules", project.getName(), "src/main/java", "content",
				project.getCapitalName() + "Portlet.java"));

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}