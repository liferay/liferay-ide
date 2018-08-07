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

import org.junit.Rule;

/**
 * @author Ashley Yuan
 * @author Lily Li
 */
public class NewFragmentWizardMavenBase extends SwtbotBase {

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

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void createFragmentWithJspf() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/blogs_admin/entry_search_columns.jspf",
			"META-INF/resources/blogs_aggregator/view_entry_content.jspf"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void createFragmentWithoutFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareMaven(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

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

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

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

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

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

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.selectFile("portlet.properties");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("portlet.properties");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}