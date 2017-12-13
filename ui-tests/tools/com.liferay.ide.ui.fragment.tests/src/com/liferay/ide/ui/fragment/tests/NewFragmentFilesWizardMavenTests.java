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
 * @author Vicky Wang
 * @author Sunny Shi
 * @author Lily Li
 */
public class NewFragmentFilesWizardMavenTests extends SwtbotBase {

	@BeforeClass
	public static void addLiferayServer() throws IOException {
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
	}

	@AfterClass
	public static void deleteRuntime() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openServerRuntimeEnvironmentsTry();

		dialogAction.serverRuntimeEnvironments.deleteRuntimeTryConfirm(_serverName);

		dialogAction.preferences.confirm();
	}

	@Test
	public void addFragmentFilesShortcuts() {
		String projectName = "test-fragment-files-shortcuts-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.journal.web");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openNewBtnFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/add_button.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "add_button.jsp"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspFile));

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/article_vertical_card.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspfFile =
			{projectName, "src", "main", "resources", "META-INF", "resources", "article_vertical_card.jspf"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspfFile));

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		String[] portletPropertiesFile = {projectName, "src", "main", "java", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.project.visibleFileTry(portletPropertiesFile));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void addFragmentJspfFiles() {
		String projectName = "test-fragment-jspf-files-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.directory.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/user/search_columns.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspfFile =
			{projectName, "src", "main", "resources", "META-INF", "resources", "user", "search_columns.jspf"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspfFile));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void addFragmentJspFiles() {
		String projectName = "test-fragment-jsp-files-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.comment.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/view_comment.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "view_comment.jsp"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspFile));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void addFragmentPortletPropertiesFiles() {
		String projectName = "test-fragment-portlet-properties-files-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.contacts.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		String[] portletPropertiesFile = {projectName, "src", "main", "java", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.project.visibleFileTry(portletPropertiesFile));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void addFragmentResourceActionFiles() {
		String projectName = "test-fragment-resource-action-files-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.dynamic.data.lists.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		String[] resourceActionFile = {projectName, "src", "main", "resources", "resource-actions", "default-ext.xml"};

		String[] portletPropertiesFile = {projectName, "src", "main", "resources", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.project.visibleFileTry(resourceActionFile));

		Assert.assertTrue(viewAction.project.visibleFileTry(portletPropertiesFile));

		viewAction.project.closeAndDelete(projectName);
	}

	@Test
	public void testFragmentFilesWithDeleteButton() {
		String projectName = "test-fragment-files-delete-maven";

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(projectName);

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.display.web");

		dialogAction.confirm();

		wizardAction.finish();

		String[] files =
			{"META-INF/resources/init-ext.jsp", "META-INF/resources/view.jsp", "META-INF/resources/init.jsp"};

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.selectFile("META-INF/resources/init-ext.jsp");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.finish();

		String[] initJspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "init.jsp"};

		String[] viewJspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "view.jsp"};

		Assert.assertTrue(viewAction.project.visibleFileTry(initJspFile));

		Assert.assertTrue(viewAction.project.visibleFileTry(viewJspFile));

		viewAction.project.closeAndDelete(projectName);
	}

	private static final String _serverName = "Liferay 7-fragment-maven";

}