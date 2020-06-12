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
import com.liferay.ide.functional.liferay.support.project.ProjectsSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceMavenSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ying Xu
 * @author Rui Wang
 */
@Ignore("ignore as unstable need more research")
public class NewComponentWizardMavenTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceMavenSupport liferayWorkspace = new LiferayWorkspaceMavenSupport(bot);

	@Test
	public void createComponentModelListener() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

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
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName, className + ".java"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentOnMultipleProjects() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(0));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projects.getName(1));

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		String className = "MyMultipleComponent";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(projects.getName(0), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", projects.getName(0),
				"src/main/java", packageName, className + ".java [Component]"));

		jobAction.waitForNoRunningJobs();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(projects.getName(0)));

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(projects.getName(1), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", projects.getName(1),
				"src/main/java", packageName, className + ".java [Component]"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(projects.getName(1)));
	}

	@Test
	public void createComponentPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyPortlet";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName, className + ".java"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyServiceWrapper";
		String packageName = "com.liferay.ide.test";
		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("com.liferay.bookmarks.service.BookmarksEntryLocalServiceWrapper");
		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName, className + ".java"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentWithPackage() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		String packageName = project.getName() + ".constants";

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText(packageName);

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName + " 1.0.0↔[1.0,2)",
				project.getCapitalName() + "Portlet.java [Component]"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createComponentWithShortcuts() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewBtnLiferayComponentClassWizard();

		String className = "MyShortcutsComponent";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName, className + ".java [Component]"));

		viewAction.project.delete(
			liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
			"src/main/java", packageName);

		jobAction.waitForNoRunningJobs();

		wizardAction.openFileMenuLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName, className + ".java [Component]"));

		viewAction.project.delete(
			liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
			"src/main/java", packageName);

		jobAction.waitForNoRunningJobs();

		viewAction.project.openComponentClassWizard(liferayWorkspace.getModuleFiles(project.getName()));

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName, className + ".java [Component]"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Test
	public void createDefaultComponent() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), REST);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		String packageName = project.getName();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), liferayWorkspace.getName() + "-modules (in modules)", project.getName(),
				"src/main/java", packageName + ".application", project.getCapitalName() + "Portlet.java [Component]"));

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}