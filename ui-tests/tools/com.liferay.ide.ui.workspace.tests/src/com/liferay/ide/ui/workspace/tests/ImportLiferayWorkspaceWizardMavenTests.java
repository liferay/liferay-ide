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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.swt.finder.SWTBotAssert;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ashley Yuan
 */
public class ImportLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	//init code is commented out in pom file now
	@Test
	public void importLiferayWorkspaceWithDownloadLiferayBundle() {
	}

	@Test
	public void importLiferayWorkspaceWithPluginsSdk() {
	}

	@Test
	public void importMavenLiferayWorkspaceProject() throws IOException {
		String workspaceName = "test-liferay-workspace-maven";

		IPath testProject = envAction.getProjectsFolder().append(workspaceName);

		File workspaceProject = envAction.prepareTempProject(testProject.toFile());

		wizardAction.openImportLiferayWorkspaceWizard();

		wizardAction.prepareImportLiferayWorkspace(workspaceProject.getPath());

		wizardAction.finish();

		viewAction.openProjectFile(workspaceName, "pom.xml");

		editorAction.switchTabPomXml();

		SWTBotAssert.assertContains(workspaceName, editorAction.getContent());
		SWTBotAssert.assertContains("artifactId", editorAction.getContent());
		SWTBotAssert.assertContains("modules", editorAction.getContent());
		SWTBotAssert.assertContains("themes", editorAction.getContent());
		SWTBotAssert.assertContains("wars", editorAction.getContent());

		editorAction.close();

		String[] moduleNames = {workspaceName, "test-liferay-workspace-maven-modules (in modules)"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(moduleNames));

		String[] themeNames = {workspaceName, "test-liferay-workspace-maven-themes (in themes)"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(themeNames));

		String[] warNames = {workspaceName, "test-liferay-workspace-maven-wars (in wars)"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(warNames));

		viewAction.closeAndDeleteProject(moduleNames);
		viewAction.closeAndDeleteProject(themeNames);
		viewAction.closeAndDeleteProject(warNames);

		viewAction.closeAndDeleteProject(workspaceName);
	}

}