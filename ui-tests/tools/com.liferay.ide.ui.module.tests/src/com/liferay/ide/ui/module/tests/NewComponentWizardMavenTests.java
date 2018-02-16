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
import com.liferay.ide.ui.liferay.base.ProjectSupport;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class NewComponentWizardMavenTests extends SwtbotBase {

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createComponentModelListener() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

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

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createComponentOnMultipleProjects() {
		String projectName1 = "test-component-on-multiple-maven1";
		String projectName2 = "test-component-on-multiple-maven2";

		wizardAction.openNewLiferayModuleWizard();
		wizardAction.newModule.prepareMaven(projectName1);
		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();
		wizardAction.newModule.prepareMaven(projectName2);
		wizardAction.finish();

		String className = "MyMultipleComponent";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.openNewLiferayComponentClassWizard();
		wizardAction.newLiferayComponent.prepare(projectName1, template, className, packageName);
		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName1, "src/main/java", packageName, className + ".java"));

		wizardAction.openNewLiferayComponentClassWizard();
		wizardAction.newLiferayComponent.prepare(projectName2, template, className, packageName);
		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(projectName2, "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(projectName1);
		viewAction.project.closeAndDelete(projectName2);
	}

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createComponentPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName());

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

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createComponentServiceWrapper() {
		wizardAction.openNewLiferayModuleWizard();
		wizardAction.newModule.prepareMaven(project.getName());
		wizardAction.finish();

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
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createComponentWithPackage() {
		wizardAction.openNewLiferayModuleWizard();
		wizardAction.newModule.prepareMaven(project.getName());
		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		String className = "TestComponentWithPackagesMavenPortlet";
		String packageName = "test.component.with.packages";

		wizardAction.newLiferayComponent.openSelectPackageNameDialog();

		dialogAction.prepareText(packageName);
		dialogAction.confirm();

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createComponentWithShortcuts() {
		wizardAction.openNewLiferayModuleWizard();
		wizardAction.newModule.prepareMaven(project.getName());
		wizardAction.finish();

		wizardAction.openNewBtnLiferayComponentClassWizard();

		String className = "MyShortcutsComponent";
		String packageName = "com.liferay.ide.test";
		String template = PORTLET_UPCASE;

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);

		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.delete(project.getName(), "src/main/java", packageName);

		wizardAction.openFileMenuLiferayComponentClassWizard();

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);
		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.delete(project.getName(), "src/main/java", packageName);

		viewAction.project.openComponentClassWizard(project.getName());

		wizardAction.newLiferayComponent.prepare(project.getName(), template, className, packageName);
		wizardAction.finish();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Ignore("ignore as it may fails but happen unstable, need more research for it")
	@Test
	public void createDefaultComponent() {
		wizardAction.openNewLiferayModuleWizard();
		wizardAction.newModule.prepareMaven(project.getName());
		wizardAction.finish();

		wizardAction.openNewLiferayComponentClassWizard();

		wizardAction.finish();

		String className = "TestComponentDefaultMavenPortlet";
		String packageName = "test.componentDefault.maven.portlet";

		Assert.assertTrue(
			viewAction.project.visibleFileTry(project.getName(), "src/main/java", packageName, className + ".java"));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}