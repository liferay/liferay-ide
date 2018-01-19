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
import com.liferay.ide.ui.swtbot.util.StringPool;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.swt.finder.SWTBotAssert;

import org.junit.Assert;
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
		String liferayWorkspaceName = "test-liferay-workspace-gradle";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareBundlesProject(testProject.toFile());

		String workspaceProjectName = workspaceProject.getName();

		envAction.unzipServerToProject(workspaceProjectName);

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath());

		wizardAction.finish();

		ide.sleepLinux(10000);

		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "bundles"));

		viewAction.project.closeAndDelete(workspaceProjectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(workspaceProjectName + " server");

		dialogAction.preferences.confirm();
	}

	@Test
	public void importLiferayWorkspaceWithDownloadBundle() throws IOException {
		if (!envAction.internal()) {
			return;
		}

		String liferayWorkspaceName = "test-liferay-workspace-gradle";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareBundlesProject(testProject.toFile());

		String workspaceProjectName = workspaceProject.getName();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath(), true, StringPool.EMPTY);

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.importLiferayWorkspace.prepareBundleUrl(bundleUrl);

		wizardAction.finish();

		ide.sleepLinux(10000);

		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "bundles"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "gradle"));

		viewAction.project.closeAndDelete(workspaceProjectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(workspaceProjectName + " server");

		dialogAction.preferences.confirm();
	}

	@Test
	public void importLiferayWorkspaceWithoutBundle() throws IOException {
		String liferayWorkspaceName = "test-liferay-workspace-gradle";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareTempProject(testProject.toFile());

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath());

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspaceName, "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspaceName, "gradle"));

		viewAction.project.openFile(liferayWorkspaceName, GRADLE_PROPERTIES);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", editorAction.getContent());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", editorAction.getContent());

		editorAction.close();

		viewAction.project.openFile(liferayWorkspaceName, SETTINGS_GRADLE);

		SWTBotAssert.assertContains("buildscript", editorAction.getContent());
		SWTBotAssert.assertContains("repositories", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(liferayWorkspaceName);
	}

	@Test
	public void importLiferayWorkspaceWithPluginsSdk() throws IOException {
		String liferayWorkspaceName = "test-liferay-workspace-gradle";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareBundlesProject(testProject.toFile());

		String workspaceProjectName = workspaceProject.getName();

		envAction.unzipPluginsSdkToProject(workspaceProjectName);

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath());

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "plugins-sdk"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "gradle"));

		viewAction.project.openFile(workspaceProjectName, GRADLE_PROPERTIES);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", editorAction.getContent());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", editorAction.getContent());

		editorAction.close();

		viewAction.project.openFile(workspaceProjectName, SETTINGS_GRADLE);

		SWTBotAssert.assertContains("buildscript", editorAction.getContent());
		SWTBotAssert.assertContains("repositories", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(workspaceProjectName, "plugins-sdk");
		viewAction.project.closeAndDelete(workspaceProjectName);
	}

	//@Ignore("Need more time to deal with bundle on Linux, will add bundle test back")
	@Test
	public void importLiferayWorkspaceWithPluginsSdkDownloadBundle() throws IOException {
		if (!envAction.internal()) {
			return;
		}

		String liferayWorkspaceName = "test-liferay-workspace-gradle";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareBundlesProject(testProject.toFile());

		String workspaceProjectName = workspaceProject.getName();

		envAction.unzipPluginsSdkToProject(workspaceProjectName);

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath(), true, StringPool.EMPTY);

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.importLiferayWorkspace.prepareBundleUrl(bundleUrl);

		wizardAction.finish();

		ide.sleepLinux(10000);

		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "plugins-sdk"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "bundles"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(workspaceProjectName, "gradle"));

		viewAction.project.openFile(workspaceProjectName, GRADLE_PROPERTIES);

		SWTBotAssert.assertContains("liferay.workspace.modules.dir", editorAction.getContent());
		SWTBotAssert.assertContains("liferay.workspace.home.dir", editorAction.getContent());

		editorAction.close();

		viewAction.project.openFile(workspaceProjectName, SETTINGS_GRADLE);

		SWTBotAssert.assertContains("buildscript", editorAction.getContent());
		SWTBotAssert.assertContains("repositories", editorAction.getContent());

		editorAction.close();

		viewAction.project.closeAndDelete(workspaceProjectName, "plugins-sdk");
		viewAction.project.closeAndDelete(workspaceProjectName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(workspaceProjectName + " server");

		dialogAction.preferences.confirm();
	}

}