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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.server.PureTomcat70Support;
import com.liferay.ide.ui.liferay.util.RuleUtil;
import com.liferay.ide.ui.swtbot.util.StringPool;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ValidationFragmentFilesTests extends SwtbotBase {

	public static PureTomcat70Support tomcat = new PureTomcat70Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRuleChain(bot, tomcat);

	@Test
	public void checkInitialState() {
		wizardAction.openFileMenuFragmentFilesWizard();

		validationAction.assertEquals(NO_SUITABLE_LIFERAY_FRAGMENT_PROJECT, wizardAction.getValidationMsg(1));

		validationAction.assertEquals(StringPool.BLANK, wizardAction.newFragmentFiles.projectName());

		// Assert.assertEquals("<None>", _newFragmentFilesWizard.getLiferyRuntimes().getText());

		validationAction.assertEnabledTrue(wizardAction.newFragmentFiles.newRuntimeBtn());

		validationAction.assertEquals(StringPool.BLANK, wizardAction.newFragmentFiles.hostOsgiBundle());

		validationAction.assertEnabledFalse(wizardAction.newFragmentFiles.addOverrideFilesBtn());

		validationAction.assertEnabledFalse(wizardAction.newFragmentFiles.deleteBtn());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();
	}

	@Test
	public void testAddAllFilesOnFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.asset.categories.navigation.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openFileMenuFragmentFilesWizard();

		String[] files = {
			"META-INF/resources/configuration.jsp", "META-INF/resources/init-ext.jsp", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp", "portlet.properties", "resource-actions/default.xml"
		};

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		dialogAction.selectItems(files);

		dialogAction.confirm();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void testAddFilesOnAllFilesFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

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

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		// wait for IDE-3566 fixed
		// Assert.assertFalse(dialogAction.getConfirmBtn().isEnabled());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void testAddFilesOnNoneFilesFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepareGradle(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.application.list.api");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openFileMenuFragmentFilesWizard();

		wizardAction.newFragmentFiles.openAddOverrideFilesDialog();

		validationAction.assertEnabledFalse(dialogAction.getConfirmBtn());

		dialogAction.cancel();

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void testAddFilesOnNonFragment() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName());

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		wizardAction.openFileMenuFragmentFilesWizard();

		validationAction.assertEquals(NO_SUITABLE_LIFERAY_FRAGMENT_PROJECT, wizardAction.getValidationMsg(1));

		validationAction.assertEnabledFalse(wizardAction.newFragmentFiles.addOverrideFilesBtn());

		validationAction.assertEnabledFalse(wizardAction.getNextBtn());

		validationAction.assertEnabledFalse(wizardAction.getFinishBtn());

		wizardAction.cancel();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}