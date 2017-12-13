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
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Sunny Shi
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ImportLiferayWorkspaceWizardGradleTests extends SwtbotBase {

	@Test
	public void importLiferayWorkspace() throws IOException {
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

	@Ignore("Failed on mac, need to fix")
	@Test
	public void importLiferayWorkspaceWithDownloadLiferayBundle() throws IOException {
		String liferayWorkspaceName = "test-liferay-workspace-gradle";

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareTempProject(testProject.toFile());

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath(), true, StringPool.EMPTY);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspaceName, "bundles"));
		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspaceName, "configs"));
		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspaceName, "gradle"));

		viewAction.project.closeAndDelete(liferayWorkspaceName);
	}

	@Test
	public void importLiferayWorkspaceWithPluginsSdk() {
	}

}