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

package com.liferay.ide.functional.fragment.wizard.base;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceSupport;

import org.junit.Assert;
import org.junit.Rule;

/**
 * @author Lily Li
 * @author Ashley Yuan
 */
public abstract class NewFragmentWizardLiferayWorkspaceGradleBase extends SwtbotBase {

	public void createFragmentChangeModulesDir() {
		viewAction.project.openFile(getLiferayWorkspace().getName(), "gradle.properties");

		StringBuffer sb = new StringBuffer();

		String previousModulesFolderSetting = "liferay.workspace.modules.dir = modules";

		String newModulesFolderName = "modulesTest";

		sb.append("liferay.workspace.modules.dir");
		sb.append("=");
		sb.append(newModulesFolderName);

		String gradlePropertiesText = editorAction.getContent();

		editorAction.setText(gradlePropertiesText.replace(previousModulesFolderSetting, sb.toString()));

		editorAction.save();

		editorAction.close();

		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

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

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(getLiferayWorkspace().getName());

		String[] projectNames = {getLiferayWorkspace().getName(), newModulesFolderName, project.getName()};

		String[] newModulesFolderNames = {getLiferayWorkspace().getName(), newModulesFolderName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		viewAction.project.closeAndDeleteFromDisk(projectNames);

		viewAction.project.closeAndDeleteFromDisk(newModulesFolderNames);

		viewAction.project.openFile(getLiferayWorkspace().getName(), "gradle.properties");

		editorAction.setText(gradlePropertiesText.replace(sb.toString(), previousModulesFolderSetting));

		editorAction.save();

		editorAction.close();
	}

	public void createFragmentWithJsp() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.layout.admin.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/edit_layout.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(getLiferayWorkspace().getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspace().getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void createFragmentWithJspf() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.site.memberships.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/role_columns.jspf");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(getLiferayWorkspace().getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspace().getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void createFragmentWithoutFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(getLiferayWorkspace().getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspace().getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void createFragmentWithPortletProperites() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.dynamic.data.mapping.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/view.jsp", "META-INF/resources/error.jsp", "META-INF/resources/init.jsp",
			"portlet.properties"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(getLiferayWorkspace().getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspace().getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void createFragmentWithResourceAction() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("resource-actions/default.xml");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspace().getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void createFragmentWithWholeFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.browser.web");

		dialogAction.confirm();

		String[] files = {
			"META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp",
			"portlet.properties"
		};

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.selectFile("META-INF/resources/init-ext.jsp");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/init-ext.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspace().getModuleFiles(project.getName())));

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	protected abstract LiferayWorkspaceSupport getLiferayWorkspace();

}