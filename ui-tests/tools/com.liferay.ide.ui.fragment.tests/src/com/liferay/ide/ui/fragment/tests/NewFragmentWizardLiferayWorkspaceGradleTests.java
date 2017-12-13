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

package com.liferay.ide.ui.fragment.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class NewFragmentWizardLiferayWorkspaceGradleTests extends SwtbotBase {

	@BeforeClass
	public static void addServerAndWorkspace() throws IOException {
		envAction.unzipServer();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.openNewRuntimeWizard();

		wizardAction.newRuntime.prepare7();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName());

		wizardAction.newRuntime7.prepare(_serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.newServer.prepare(_serverName);

		wizardAction.finish();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(_liferayWorkspaceName);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(_liferayWorkspaceName));
	}

	@AfterClass
	public static void cleanWorkspaceAndServer() {
		String[] modulesFolderNames = {_liferayWorkspaceName, "modules"};

		viewAction.project.closeAndDelete(modulesFolderNames);

		viewAction.project.closeAndDelete(_liferayWorkspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(_serverName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void createFragmentChangeModulesDir() {
		viewAction.project.openFile(_liferayWorkspaceName, "gradle.properties");

		StringBuffer sb = new StringBuffer();

		String newModulesFolderName = "modulesTest";

		sb.append("liferay.workspace.modules.dir");
		sb.append("=");
		sb.append(newModulesFolderName);

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();

		String projectName = "test-fragment-change-modules-dir";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.site.navigation.site.map.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, newModulesFolderName, projectName};

		String[] newModulesFolderNames = {_liferayWorkspaceName, newModulesFolderName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);

		viewAction.project.closeAndDeleteFromDisk(newModulesFolderNames);

		viewAction.project.openFile(_liferayWorkspaceName, "gradle.properties");

		sb.delete(0, sb.length());
		sb.append("liferay.workspace.modules.dir=modules");

		editorAction.setText(sb.toString());

		editorAction.save();

		editorAction.close();
	}

	@Test
	public void createFragmentWithJsp() {
		String projectName = "test-fragment-jsp-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.layout.admin.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/add_layout.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFragmentWithJspf() {
		String projectName = "test-fragment-jspf-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.site.memberships.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/role_columns.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFragmentWithoutFiles() {
		String projectName = "test-fragment-without-files-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFragmentWithPortletProperites() {
		String projectName = "test-fragment-portlet-properties-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.dynamic.data.mapping.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/template_add_buttons.jsp", "META-INF/resources/error.jsp",
			"META-INF/resources/init.jsp", "portlet.properties"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFragmentWithResourceAction() {
		String projectName = "test-fragment-resource-action-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	@Test
	public void createFragmentWithWholeFiles() {
		String projectName = "test-fragment-whole-files-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.xsl.content.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.selectFile("META-INF/resources/configuration.jsp");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/configuration.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);
	}

	private static final String _liferayWorkspaceName = "test-liferay-workspace-gradle";
	private static final String _serverName = "Liferay 7-fragment-gradle";

}