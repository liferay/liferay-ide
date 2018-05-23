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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.project.ProjectsSupport;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Rui Wang
 */
public class NewComponentWizardGradleTests extends SwtbotBase {

	@Test
	public void createComponentBrowsePackage() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = project.getName().substring(0, 1).toUpperCase() + project.getName().substring(1);
		String packageName = project.getName();

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText(packageName);

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				project.getName(), "src/main/java", packageName, className + "Portlet.java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createComponentModelListener() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET);

		wizardAction.finish();

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
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createComponentOnMultipleProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projects.getName(0));

		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projects.getName(1));

		wizardAction.finish();

		String className = projects.getName(0).substring(0, 1).toUpperCase() + projects.getName(0).substring(1);

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				projects.getName(0), "src/main/java", "content", className + "Portlet.java"));

		editorAction.close();

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.prepareProjectName(projects.getName(1));

		wizardAction.finish();

		className = projects.getName(1).substring(0, 1).toUpperCase() + projects.getName(1).substring(1);

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				projects.getName(1), "src/main/java", "content", className + "Portlet.java"));

		viewAction.project.closeAndDelete(projects.getName(0));

		viewAction.project.closeAndDelete(projects.getName(1));
	}

	@Test
	public void createComponentPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyPortlet";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("ignore to wait target platform way")
	@Test
	public void createComponentServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyServiceWrapper";
		String packageName = "com.liferay.ide.test";
		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*bookmarksEntryLocal");

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createComponentShortcuts() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		String className = project.getName().substring(0, 1).toUpperCase() + project.getName().substring(1);

		wizardAction.openFileMenuLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(MVC_PORTLET_UPCASE);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				project.getName(), "src/main/java", "content", className + "MVCPortlet.java"));

		editorAction.close();

		viewAction.project.openComponentClassWizard(project.getName());

		wizardAction.newLiferayComponent.prepare(REST_UPCASE);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				project.getName(), "src/main/java", "content", className + "RestService.java"));

		editorAction.close();

		wizardAction.openNewBtnLiferayComponentClassWizard();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				project.getName(), "src/main/java", "content", className + "Portlet.java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createDefaultComponent() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		String className = project.getName().substring(0, 1).toUpperCase() + project.getName().substring(1);
		String packageName = "content";

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				project.getName(), "src/main/java", packageName, className + "Portlet.java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Rule
	public ProjectsSupport projects = new ProjectsSupport(bot);

}