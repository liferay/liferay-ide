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
public class NewFragmentWizardLiferayWorkspaceMavenTests extends SwtbotBase {

	@BeforeClass
	public static void addServerAndWorkspace() throws IOException {
		envAction.unzipServer();

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName());

		wizardAction.prepareLiferay7RuntimeInfo(_serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(_serverName);

		wizardAction.finish();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(_liferayWorkspaceName);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(_liferayWorkspaceName));
	}

	@AfterClass
	public static void cleanWorkspaceAndServer() {
		String themesFolderName = _liferayWorkspaceName + "-themes (in themes)";

		String warsFolderName = _liferayWorkspaceName + "-wars (in wars)";

		String[] modulesFolderNames = {_liferayWorkspaceName, _modulesFolderName};

		String[] themesFolderNames = {_liferayWorkspaceName, themesFolderName};

		String[] warsFolderNames = {_liferayWorkspaceName, warsFolderName};

		viewAction.closeAndDeleteProject(modulesFolderNames);

		viewAction.closeAndDeleteProject(themesFolderNames);

		viewAction.closeAndDeleteProject(warsFolderNames);

		viewAction.closeAndDeleteProject(_liferayWorkspaceName);

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.deleteRuntimeTryConfirm(_serverName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void createFragmentWithJsp() {
		String projectName = "test-fragment-jsp-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentMaven(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.iframe.web");

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/proxy.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, _modulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);
	}

	@Test
	public void createFragmentWithJspf() {
		String projectName = "test-fragment-jspf-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentMaven(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.site.memberships.web");

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/role_columns.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, _modulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);
	}

	@Test
	public void createFragmentWithoutFiles() {
		String projectName = "test-fragment-without-files-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentMaven(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.frontend.image.editor.web");

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, _modulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);
	}

	@Test
	public void createFragmentWithPortletProperites() {
		String projectName = "test-fragment-portlet-properties-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentMaven(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.item.selector.web");

		dialogAction.confirm();

		String[] files = {"META-INF/resources/init.jsp", "META-INF/resources/view.jsp", "portlet.properties"};

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, _modulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);
	}

	@Test
	public void createFragmentWithResourceAction() {
		String projectName = "test-fragment-resource-action-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentMaven(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.layout.admin.web");

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, _modulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);
	}

	@Test
	public void createFragmentWithWholeFiles() {
		String projectName = "test-fragment-whole-files-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentMaven(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.site.navigation.language.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.selectFragmentFile("META-INF/resources/configuration.jsp");

		wizardAction.deleteFragmentFile();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/configuration.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] projectNames = {_liferayWorkspaceName, _modulesFolderName, projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		viewAction.closeAndDeleteProject(projectNames);
	}

	private static final String _liferayWorkspaceName = "test-liferay-workspace-maven";
	private static final String _modulesFolderName = _liferayWorkspaceName + "-modules (in modules)";
	private static final String _serverName = "Liferay 7-fragment-maven";

}