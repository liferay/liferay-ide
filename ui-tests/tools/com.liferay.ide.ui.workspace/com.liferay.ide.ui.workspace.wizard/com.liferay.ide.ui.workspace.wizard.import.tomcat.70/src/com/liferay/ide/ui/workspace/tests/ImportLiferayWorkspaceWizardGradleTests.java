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

package com.liferay.ide.ui.workspace.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ImportLiferayWorkspaceProjectSupport;

import java.io.IOException;

import org.eclipse.swtbot.swt.finder.SWTBotAssert;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sunny Shi
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Lily Li
 */
public class ImportLiferayWorkspaceWizardGradleTests extends SwtbotBase {

	@Test
	public void importLiferayWorkspaceWithBundle() throws IOException {
		project.prepareServer();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.importLiferayWorkspace.prepareServerName(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		ide.sleepLinux(10000);

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "bundles"));

		viewAction.project.closeAndDelete(project.getName());

		// TODO need to check with Charles

		// dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay Community Edition Portal 7.0.4 GA5");

		dialogAction.deleteRuntimFromPreferences(project.getName());
	}

	@Test
	public void importLiferayWorkspaceWithDownloadBundle() throws IOException {
		if (!envAction.internal()) {
			return;
		}

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(project.getPath(), true, project.getName());

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.importLiferayWorkspace.prepareBundleUrl(bundleUrl);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		ide.sleepLinux(10000);

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "bundles"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "gradle"));

		viewAction.project.closeAndDelete(project.getName());

		dialogAction.deleteRuntimFromPreferences(project.getName());
	}

	@Test
	public void importLiferayWorkspaceWithoutBundle() throws IOException {
		String projectName = project.getName();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(projectName, "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(projectName, "gradle"));

		viewAction.project.openFile(projectName, GRADLE_PROPERTIES);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", editorAction.getContent());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", editorAction.getContent());

		editorAction.close();

		viewAction.project.openFile(projectName, SETTINGS_GRADLE);

		SWTBotAssert.assertContains("buildscript", editorAction.getContent());
		SWTBotAssert.assertContains("repositories", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void importLiferayWorkspaceWithPluginsSdk() throws IOException {
		project.prepareSdk();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.importLiferayWorkspace.prepareServerName(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "plugins-sdk"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "gradle"));

		viewAction.project.openFile(project.getName(), GRADLE_PROPERTIES);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", editorAction.getContent());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", editorAction.getContent());

		editorAction.close();

		viewAction.project.openFile(project.getName(), SETTINGS_GRADLE);

		SWTBotAssert.assertContains("buildscript", editorAction.getContent());
		SWTBotAssert.assertContains("repositories", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(project.getName(), "plugins-sdk");
		viewAction.project.closeAndDelete(project.getName());

		dialogAction.deleteRuntimFromPreferences(project.getName());
	}

	//@Ignore("Need more time to deal with bundle on Linux, will add bundle test back")
	@Test
	public void importLiferayWorkspaceWithPluginsSdkDownloadBundle() throws IOException {
		if (!envAction.internal()) {
			return;
		}

		project.prepareSdkOnly();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(project.getPath(), true, project.getName());

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.importLiferayWorkspace.prepareBundleUrl(bundleUrl);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		ide.sleepLinux(10000);

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "plugins-sdk"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "bundles"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName(), "gradle"));

		viewAction.project.openFile(project.getName(), GRADLE_PROPERTIES);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", editorAction.getContent());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", editorAction.getContent());

		editorAction.close();

		viewAction.project.openFile(project.getName(), SETTINGS_GRADLE);

		SWTBotAssert.assertContains("buildscript", editorAction.getContent());
		SWTBotAssert.assertContains("repositories", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(project.getName(), "plugins-sdk");
		viewAction.project.closeAndDelete(project.getName());

		dialogAction.deleteRuntimFromPreferences(project.getName());
	}

	@Rule
	public ImportLiferayWorkspaceProjectSupport project = new ImportLiferayWorkspaceProjectSupport(
		bot, "test-liferay-workspace-gradle");

}