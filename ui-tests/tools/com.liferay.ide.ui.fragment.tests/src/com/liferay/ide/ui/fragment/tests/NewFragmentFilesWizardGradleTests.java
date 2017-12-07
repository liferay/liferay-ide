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
 * @author Rui Wang
 */
public class NewFragmentFilesWizardGradleTests extends SwtbotBase {

	@BeforeClass
	public static void addLiferayServer() throws IOException {
		envAction.unzipServer();

		String serverName = "Liferay 7-fragment-gradle";

		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.openNewRuntimeWizard();

		wizardAction.prepareLiferay7RuntimeType();

		wizardAction.next();

		IPath serverDir = envAction.getLiferayServerDir();

		IPath fullServerDir = serverDir.append(envAction.getLiferayPluginServerName());

		wizardAction.prepareLiferay7RuntimeInfo(serverName, fullServerDir.toOSString());

		wizardAction.finish();

		dialogAction.confirmPreferences();

		wizardAction.openNewLiferayServerWizard();

		wizardAction.prepareNewServer(serverName);

		wizardAction.finish();
	}

	@AfterClass
	public static void deleteRuntime() {
		dialogAction.openPreferencesDialog();

		dialogAction.openServerRuntimeEnvironmentsDialogTry();

		dialogAction.deleteRuntimeTryConfirm(_serverName);

		dialogAction.confirmPreferences();
	}

	@Test
	public void addFragmentJspFiles() {
		String projectName = "test-fragment-jsp-files-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.browser.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.openFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/init-ext.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "init-ext.jsp"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(jspFile));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void addFragmentJspfFiles() {
		String projectName = "test-fragment-jspf-files-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.admin.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.openFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/edit_vocabulary_settings.jspf");

		dialogAction.confirm();

		wizardAction.finish();
		
		String[] jspfFile = {projectName, "src", "main", "resources", "META-INF", "resources", "edit_vocabulary_settings.jspf"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(jspfFile));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void addFragmentPortletPropertiesFiles() {
		String projectName = "test-fragment-portlet-files-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.announcements.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.openFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		String[] protletFile = {projectName, "src", "main", "java", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(protletFile));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void addFragmentResourceActionFiles() {
		String projectName = "test-fragment-resource-files-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.publisher.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.openFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		String[] resourceActionFile = {projectName, "src", "main", "resources", "resource-actions", "default-ext.xml"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(resourceActionFile));

		viewAction.closeAndDeleteProject(projectName);
	}

	@Test
	public void testFragmentFilesWithDeleteFuction() {
		String projectName = "test-fragment-files-delete-fuction-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.display.web");

		dialogAction.confirm();

		wizardAction.finish();

		String[] files = {"META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp"
		};

		viewAction.openFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.selectFragmentFile("META-INF/resources/init-ext.jsp");

		wizardAction.deleteFragmentFile();

		wizardAction.openAddOverrideFilesDialog();

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.finish();

		String[] viewjspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "view.jsp"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(viewjspFile));

		String[] initFile = {projectName, "src", "main", "resources", "META-INF", "resources", "init.jsp"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(initFile));

		viewAction.closeAndDeleteProject(projectName);

	}

	@Test
	 public void addFragmentFilesShortcuts() {
		String projectName = "test-fragment-files-shortcuts-gradle";

		wizardAction.openNewFragmentWizard();

		wizardAction.prepareFragmentGradle(projectName);

		wizardAction.next();

		wizardAction.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.journal.web");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openNewBtnFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/add_button.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] buttonjspFile = {projectName, "src", "main", "resources", "META-INF", "resources", "add_button.jsp"};
		
		Assert.assertTrue(viewAction.visibleProjectFileTry(buttonjspFile));
		
		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/article/display_page.jsp");

		dialogAction.confirm();

		wizardAction.finish();
		
		String[] pageFile = {projectName, "src", "main", "resources", "META-INF", "resources", "article", "display_page.jsp"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(pageFile));

		viewAction.openFragmentFilesWizard();
		
		wizardAction.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();
		
		String[] propertiesFile = {projectName, "src", "main", "java", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(propertiesFile));

		viewAction.closeAndDeleteProject(projectName);
	}
	
	private static final String _serverName = "Liferay 7-fragment-gradle";

}