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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Rui Wang
 */
public class NewComponentWizardGradleTests extends SwtbotBase {

	@Test
	public void createComponentModelListener() {
		String projectName = "test-component-module-listener-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, MVC_PORTLET);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyListener";
		String packageName = "com.liferay.ide.test";
		String template = MODEL_LISTENER;

		wizardAction.newLiferayComponent.prepare(projectName, template, className, packageName);

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*com.liferay.blogs.kernel.model.BlogsEntry");

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createComponentOnMultipleProject() {
		String firstProjectName = "first-component-gradle";
		String secondProjectName = "second-component-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(firstProjectName);

		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(secondProjectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				firstProjectName, "src/main/java", "content", "FirstComponentGradlePortlet.java"));

		editorAction.close();

		wizardAction.openNewLiferayComponentClassWizard();

		Assert.assertFalse(wizardAction.getFinishBtn().isEnabled());

		wizardAction.newLiferayComponent.prepareProjectName(secondProjectName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				secondProjectName, "src/main/java", "content", "SecondComponentGradlePortlet.java"));

		viewAction.project.closeAndDelete(firstProjectName);

		viewAction.project.closeAndDelete(secondProjectName);
	}

	@Test
	public void createComponentPortlet() {
		String projectName = "test-component-portlet-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyPortlet";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.newLiferayComponent.prepare(projectName, template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(projectName);
	}

	@Ignore("ignore to wait target platform way")
	@Test
	public void createComponentServiceWrapper() {
		String projectName = "test-component-service-wrapper-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "MyServiceWrapper";
		String packageName = "com.liferay.ide.test";
		String template = SERVICE_WRAPPER_UPCASE;

		wizardAction.newLiferayComponent.prepare(projectName, template, className, packageName);

		wizardAction.newLiferayComponent.openSelectModelClassAndServiceDialog();

		dialogAction.prepareText("*bookmarksEntryLocal");

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createComponentShortcuts() {
		String projectName = "shortcut-component-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openFileMenuLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(MVC_PORTLET_UPCASE);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				projectName, "src/main/java", "content", "ShortcutComponentGradleMVCPortlet.java"));

		editorAction.close();

		viewAction.project.openComponentClassWizard(projectName);

		wizardAction.newLiferayComponent.prepare(REST_UPCASE);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				projectName, "src/main/java", "content", "ShortcutComponentGradleRestService.java"));

		editorAction.close();

		wizardAction.openNewBtnLiferayComponentClassWizard();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				projectName, "src/main/java", "content", "ShortcutComponentGradlePortlet.java"));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createComponentWithPackage() {
		String projectName = "test-component-with-packages-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "TestComponentWithPackagesGradlePortlet";
		String packageName = "test.component.with.packages.gradle.constants";

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText(packageName);

		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void createDefaultComponent() {
		String projectName = "test-componentDefault-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName);

		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		String className = "TestComponentdefaultGradlePortlet";
		String packageName = "content";

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName, "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(projectName);
	}

}