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
 * @author Ashley Yuan
 * @author Lily Li
 */
public class ImportLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	@Test
	public void importLiferayWorkspaceWithBundle() throws IOException {
		String liferayWorkspaceName = "test-liferay-workspace-maven";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareBundlesProject(testProject.toFile());

		String workspaceProjectName = workspaceProject.getName();

		envAction.unzipServerToProject(workspaceProjectName);

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath());

		wizardAction.finish();

		String eclipseWorkspaceName = liferayWorkspaceName + " (in " + workspaceProjectName + ")";

		Assert.assertTrue(viewAction.project.visibleFileTry(eclipseWorkspaceName, "bundles"));

		String[] moduleNames = {eclipseWorkspaceName, liferayWorkspaceName + "-modules (in modules)"};
		String[] themeNames = {eclipseWorkspaceName, liferayWorkspaceName + "-themes (in themes)"};
		String[] warNames = {eclipseWorkspaceName, liferayWorkspaceName + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(eclipseWorkspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay Community Edition Portal 7.0.3 GA4");

		dialogAction.preferences.confirm();
	}

	//init code is commented out in pom file now
	@Ignore("wait for IDE-3691 fixed")
	@Test
	public void importLiferayWorkspaceWithDownloadBundle() throws IOException {
		if (!envAction.internal()) {
			return;
		}

		String liferayWorkspaceName = "test-liferay-workspace-maven";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareBundlesProject(testProject.toFile());

		String workspaceProjectName = workspaceProject.getName();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath(), true, StringPool.EMPTY);

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.importLiferayWorkspace.prepareBundleUrl(bundleUrl);

		wizardAction.finish();

		String eclipseWorkspaceName = liferayWorkspaceName + " (in " + workspaceProjectName + ")";

		Assert.assertTrue(viewAction.project.visibleFileTry(eclipseWorkspaceName, "bundles"));

		String[] moduleNames = {eclipseWorkspaceName, liferayWorkspaceName + "-modules (in modules)"};
		String[] themeNames = {eclipseWorkspaceName, liferayWorkspaceName + "-themes (in themes)"};
		String[] warNames = {eclipseWorkspaceName, liferayWorkspaceName + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(eclipseWorkspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(workspaceProjectName + " server");

		dialogAction.preferences.confirm();
	}

	@Test
	public void importLiferayWorkspaceWithoutBundle() throws IOException {
		String liferayWorkspaceName = "test-liferay-workspace-maven";

		IPath testProject = envAction.getProjectsFolder().append(liferayWorkspaceName);

		File workspaceProject = envAction.prepareTempProject(testProject.toFile());

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(workspaceProject.getPath());

		wizardAction.finish();

		viewAction.project.openFile(liferayWorkspaceName, "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		SWTBotAssert.assertContains(liferayWorkspaceName, editorAction.getContent());
		SWTBotAssert.assertContains("artifactId", editorAction.getContent());
		SWTBotAssert.assertContains("modules", editorAction.getContent());
		SWTBotAssert.assertContains("themes", editorAction.getContent());
		SWTBotAssert.assertContains("wars", editorAction.getContent());

		editorAction.close();

		String[] moduleNames = {liferayWorkspaceName, liferayWorkspaceName + "-modules (in modules)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(moduleNames));

		String[] themeNames = {liferayWorkspaceName, liferayWorkspaceName + "-themes (in themes)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(themeNames));

		String[] warNames = {liferayWorkspaceName, liferayWorkspaceName + "-wars (in wars)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(warNames));

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(liferayWorkspaceName);
	}

}