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
import com.liferay.ide.ui.liferay.base.ImportLiferayWorkspaceProjectSupport;

import java.io.IOException;

import org.eclipse.swtbot.swt.finder.SWTBotAssert;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ashley Yuan
 * @author Lily Li
 */
public class ImportLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	@Test
	public void importLiferayWorkspaceWithBundle() throws IOException {
		String liferayWorkspaceName = "test-liferay-workspace-maven";

		String projectName = project.getName();

		project.prepareServer();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.importLiferayWorkspace.prepareServerName(project.getName());

		wizardAction.finish();

		String eclipseWorkspaceName = liferayWorkspaceName + " (in " + projectName + ")";

		Assert.assertTrue(viewAction.project.visibleFileTry(eclipseWorkspaceName, "bundles"));

		String[] moduleNames = {eclipseWorkspaceName, liferayWorkspaceName + "-modules (in modules)"};
		String[] themeNames = {eclipseWorkspaceName, liferayWorkspaceName + "-themes (in themes)"};
		String[] warNames = {eclipseWorkspaceName, liferayWorkspaceName + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(eclipseWorkspaceName);

		// TODO need to check with Charles

		// dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm("Liferay Community Edition Portal 7.0.4 GA5");

		dialogAction.deleteRuntimFromPreferences(projectName);
	}

	//init code is commented out in pom file now
	@Ignore("wait for IDE-3691 fixed")
	@Test
	public void importLiferayWorkspaceWithDownloadBundle() throws IOException {
		if (!envAction.internal()) {
			return;
		}

		String liferayWorkspaceName = "test-liferay-workspace-maven";

		String projectName = project.getName();

		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepare(project.getPath(), true, project.getName());

		String bundleUrl =
			"http://ide-resources-site/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		wizardAction.importLiferayWorkspace.prepareBundleUrl(bundleUrl);

		wizardAction.finish();

		String eclipseWorkspaceName = liferayWorkspaceName + " (in " + projectName + ")";

		Assert.assertTrue(viewAction.project.visibleFileTry(eclipseWorkspaceName, "bundles"));

		String[] moduleNames = {eclipseWorkspaceName, liferayWorkspaceName + "-modules (in modules)"};
		String[] themeNames = {eclipseWorkspaceName, liferayWorkspaceName + "-themes (in themes)"};
		String[] warNames = {eclipseWorkspaceName, liferayWorkspaceName + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(eclipseWorkspaceName);

		dialogAction.deleteRuntimFromPreferences(projectName);
	}

	@Test
	public void importLiferayWorkspaceWithoutBundle() throws IOException {
		wizardAction.importProject.openImportLiferayWorkspaceWizard();

		wizardAction.importLiferayWorkspace.prepareLocation(project.getPath());

		wizardAction.finish();

		String liferayWorkspaceName = "test-liferay-workspace-maven";

		String eclipseWorkspaceName = liferayWorkspaceName + " (in " + project.getName() + ")";

		viewAction.project.openFile(eclipseWorkspaceName, "pom.xml");

		editorAction.pomXml.switchTabPomXml();

		SWTBotAssert.assertContains(liferayWorkspaceName, editorAction.getContent());
		SWTBotAssert.assertContains("artifactId", editorAction.getContent());
		SWTBotAssert.assertContains("modules", editorAction.getContent());
		SWTBotAssert.assertContains("themes", editorAction.getContent());
		SWTBotAssert.assertContains("wars", editorAction.getContent());

		editorAction.close();

		String[] moduleNames = {eclipseWorkspaceName, liferayWorkspaceName + "-modules (in modules)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(moduleNames));

		String[] themeNames = {eclipseWorkspaceName, liferayWorkspaceName + "-themes (in themes)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(themeNames));

		String[] warNames = {eclipseWorkspaceName, liferayWorkspaceName + "-wars (in wars)"};

		Assert.assertTrue(viewAction.project.visibleFileTry(warNames));

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);

		viewAction.project.closeAndDelete(eclipseWorkspaceName);
	}

	@Rule
	public ImportLiferayWorkspaceProjectSupport project = new ImportLiferayWorkspaceProjectSupport(
		bot, "test-liferay-workspace-maven");

}