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

package com.liferay.ide.ui.fragment.wizard.base;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;

import org.junit.Assert;
import org.junit.Rule;

/**
 * @author Vicky Wang
 * @author Sunny Shi
 * @author Rui Wang
 * @author Ying Xu
 */
public class NewFragmentFilesWizardMavenBase extends SwtbotBase {

	public void addFragmentFilesShortcuts() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.journal.web");

		dialogAction.confirm();

		wizardAction.finish();

		wizardAction.openNewBtnFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/add_button.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspFile = {project.getName(), "src", "main", "resources", "META-INF", "resources", "add_button.jsp"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspFile));

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/article_vertical_card.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspfFile =
			{project.getName(), "src", "main", "resources", "META-INF", "resources", "article_vertical_card.jspf"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspfFile));

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		String[] portletPropertiesFile = {project.getName(), "src", "main", "java", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.project.visibleFileTry(portletPropertiesFile));

		viewAction.project.closeAndDelete(project.getName());
	}

	public void addFragmentJspfFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.directory.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/user/search_columns.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspfFile =
			{project.getName(), "src", "main", "resources", "META-INF", "resources", "user", "search_columns.jspf"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspfFile));

		viewAction.project.closeAndDelete(project.getName());
	}

	public void addFragmentJspFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.comment.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/view_comment.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		String[] jspFile = {project.getName(), "src", "main", "resources", "META-INF", "resources", "view_comment.jsp"};

		Assert.assertTrue(viewAction.project.visibleFileTry(jspFile));

		viewAction.project.closeAndDelete(project.getName());
	}

	public void addFragmentPortletPropertiesFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.contacts.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		String[] portletPropertiesFile = {project.getName(), "src", "main", "java", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.project.visibleFileTry(portletPropertiesFile));

		viewAction.project.closeAndDelete(project.getName());
	}

	public void addFragmentResourceActionFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.dynamic.data.lists.web");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		String[] resourceActionFile =
			{project.getName(), "src", "main", "resources", "resource-actions", "default-ext.xml"};

		String[] portletPropertiesFile = {project.getName(), "src", "main", "resources", "portlet-ext.properties"};

		Assert.assertTrue(viewAction.project.visibleFileTry(resourceActionFile));

		Assert.assertTrue(viewAction.project.visibleFileTry(portletPropertiesFile));

		viewAction.project.closeAndDelete(project.getName());
	}

	public void testFragmentFilesWithDeleteButton() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.display.web");

		dialogAction.confirm();

		wizardAction.finish();

		String[] files =
			{"META-INF/resources/init-ext.jsp", "META-INF/resources/view.jsp", "META-INF/resources/init.jsp"};

		viewAction.project.openFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.newFragmentFiles.selectFile("META-INF/resources/init-ext.jsp");

		wizardAction.newFragmentFiles.deleteFile();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		Assert.assertTrue(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.finish();

		String[] initJspFile = {project.getName(), "src", "main", "resources", "META-INF", "resources", "init.jsp"};

		String[] viewJspFile = {project.getName(), "src", "main", "resources", "META-INF", "resources", "view.jsp"};

		Assert.assertTrue(viewAction.project.visibleFileTry(initJspFile));

		Assert.assertTrue(viewAction.project.visibleFileTry(viewJspFile));

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}
