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
import com.liferay.ide.ui.liferay.base.ProjectSupport;
import com.liferay.ide.ui.liferay.base.TomcatSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Sunny Shi
 * @author Ying Xu
 */
public class NewFragmentWizardMavenTests extends SwtbotBase {

	@ClassRule
	public static TomcatSupport tomcat = new TomcatSupport(bot);

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	@Test
	public void createFragmentWithJsp() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/blogs_admin/configuration.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createFragmentWithJspf() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/blogs_admin/entry_search_columns.jspf",
			"META-INF/resources/blogs_admin/entry_search_results.jspf"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createFragmentWithoutFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createFragmentWithPortletProperites() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		String[] files = {"META-INF/resources/blogs_admin/configuration.jsp", "portlet.properties"};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createFragmentWithResourceAction() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createFragmentWithWholeFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.navigation.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.selectFile("portlet.properties");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.project.closeAndDelete(project.getName());
	}

}