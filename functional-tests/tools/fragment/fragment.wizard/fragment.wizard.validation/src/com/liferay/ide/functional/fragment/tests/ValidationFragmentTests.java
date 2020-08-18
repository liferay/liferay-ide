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

package com.liferay.ide.functional.fragment.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.server.LiferaryWorkspaceTomcat70Support;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle70Support;
import com.liferay.ide.functional.liferay.util.RuleUtil;
import com.liferay.ide.functional.liferay.util.ValidationMsg;
import com.liferay.ide.functional.swtbot.util.StringPool;

import java.io.File;

import java.util.Objects;

import org.eclipse.core.runtime.Platform;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Ashley Yuan
 * @author Lily Li
 */
public class ValidationFragmentTests extends SwtbotBase {

	public static LiferayWorkspaceGradle70Support liferayWorkspace = new LiferayWorkspaceGradle70Support(bot);
	public static LiferaryWorkspaceTomcat70Support server = new LiferaryWorkspaceTomcat70Support(bot, liferayWorkspace);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat70LiferayWorkspaceRuleChain(bot, liferayWorkspace, server);

	@Ignore("Ignore because of the change caused by IDE-4789")
	@Test
	public void checkBuildType() {
		wizardAction.openNewFragmentWizard();

		String[] expectedBuildTypes = {GRADLE, MAVEN};

		validationAction.assertEquals(expectedBuildTypes, wizardAction.newProject.buildType());

		wizardAction.cancel();
	}

	@Test
	public void checkHostOsgiBundle() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("gg");

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.prepareText("*blogs");

		dialogAction.selectTableItem("com.liferay.blogs.api-3.0.1.jar");

		dialogAction.selectTableItem("com.liferay.blogs.web-1.1.18.jar");

		dialogAction.selectTableItem("com.liferay.microblogs.web-2.0.15.jar");

		validationAction.assertEnabledTrue(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.cancel();
	}

	@Test
	public void checkInfoInitialState() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newFragmentInfo.getHostOsgiBundle());

		validationAction.assertEnabledTrue(wizardAction.newFragmentInfo.getBrowseOsgiBtn());

		validationAction.assertEquals(HOST_OSGI_BUNDLE_MUST_BE_SPECIFIED, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.newFragmentInfo.getAddOverrideFilesBtn());

		validationAction.assertEnabledFalse(wizardAction.newFragmentInfo.deleteBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void checkInitialState() {
		wizardAction.openNewFragmentWizard();

		validationAction.assertTextEquals(StringPool.BLANK, wizardAction.newProject.projectName());

		validationAction.assertEquals(PLEASE_ENTER_A_PROJECT_NAME, wizardAction.getValidationMsg(2));

		validationAction.assertCheckedTrue(wizardAction.newProject.useDefaultLocation());

		wizardAction.newProject.deselectUseDefaultLocation();

		String workspacePath = envAction.getEclipseWorkspacePathOSString();

		if (Objects.equals("win32", Platform.getOS())) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		validationAction.assertTextEquals(workspacePath, wizardAction.newProject.location());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Ignore("Ignore because of the change caused by IDE-4789")
	@Test
	public void checkLocation() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.newFragment.deselectUseDefaultLocation();

		String workspacePath = envAction.getEclipseWorkspacePathOSString();

		if (Objects.equals("win32", Platform.getOS())) {
			workspacePath = workspacePath.replaceAll("\\\\", "/");
		}

		validationAction.assertTextEquals(workspacePath, wizardAction.newProject.location());

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-fragment-wizard-project-location.csv"))) {

			String os = msg.getOs();

			if (!os.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newFragment.prepareLocation(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.newFragment.prepareGradle(project.getName(), workspacePath + "/testLocation");

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void checkOverridenFiles() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		String[] files = {
			"META-INF/resources/blogs_admin/configuration.jsp", "META-INF/resources/blogs_admin/entry_action.jsp",
			"META-INF/resources/blogs_admin/entry_search_columns.jspf", "resource-actions/default.xml",
			"portlet.properties"
		};

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentInfo.selectFile("META-INF/resources/blogs_admin/configuration.jsp");

		wizardAction.newFragmentInfo.deleteFile();

		wizardAction.cancel();
	}

	@Test
	public void checkProjectName() {
		wizardAction.openNewFragmentWizard();

		for (ValidationMsg msg :
				envAction.getValidationMsgs(
					new File(envAction.getValidationDir(), "new-fragment-wizard-project-name.csv"))) {

			String os = msg.getOs();

			if (!os.equals(Platform.getOS())) {
				continue;
			}

			wizardAction.newFragment.prepareProjectName(msg.getInput());

			validationAction.assertEquals(msg.getExpect(), wizardAction.getValidationMsg(2));
		}

		wizardAction.cancel();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}