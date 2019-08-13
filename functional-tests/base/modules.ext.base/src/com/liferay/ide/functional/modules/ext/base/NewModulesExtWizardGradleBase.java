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

package com.liferay.ide.functional.modules.ext.base;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceIndexSourcesGradleSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Rui Wang
 * @author Ashley Yuan
 */
public class NewModulesExtWizardGradleBase extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceIndexSourcesGradleSupport liferayWorkspace =
		new LiferayWorkspaceIndexSourcesGradleSupport(bot);

	public void addModulesExtWithJava() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile(
			"com", "liferay", "login", "web", "internal", "constants", "LoginPortletKeys.java");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/java",
				"com.liferay.login.web.internal.constants", "LoginPortletKeys.java"));
	}

	public void addModulesExtWithJSP() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("META-INF", "resources", "configuration.jsp");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "META-INF", "resources",
				"configuration.jsp"));
	}

	public void addModulesExtWithLanguageProperties() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("content", "Language.properties");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "content",
				"Language.properties"));
	}

	public void addModulesExtWithManifest() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("META-INF", "MANIFEST.MF");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "META-INF", "MANIFEST.MF"));
	}

	public void addModulesExtWithoutOverrideFiles() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();
	}

	public void addModulesExtWithPortletProperties() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("portlet.properties");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "portlet.properties"));
	}

	public void addModulesExtWithResourceAction() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay:com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.next();

		wizardAction.newModulesExt.openAddOriginMoudleDialog();

		dialogAction.selectOverrideFile("resource-actions", "default.xml");

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getName(), "ext", project.getName(), "src/main/resources", "resource-actions",
				"default.xml"));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}