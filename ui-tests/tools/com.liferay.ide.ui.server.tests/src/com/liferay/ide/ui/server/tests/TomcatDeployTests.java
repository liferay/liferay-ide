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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.ProjectSupport;
import com.liferay.ide.ui.liferay.base.TomcatRunningSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class TomcatDeployTests extends SwtbotBase {

	@ClassRule
	public static TomcatRunningSupport tomcat = new TomcatRunningSupport(bot);

	@Test
	public void deployFragment() {
		wizardAction.openNewFragmentWizard();

		wizardAction.newFragment.prepare(project.getName());

		wizardAction.next();

		wizardAction.newFragmentInfo.openBrowseOsgiBundleDialog();

		dialogAction.prepareText("com.liferay.blogs.web");

		dialogAction.confirm();

		wizardAction.newFragmentInfo.openAddOverrideFilesDialog();

		dialogAction.selectItems("META-INF/resources/init.jsp");

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(tomcat.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STOPPED com.liferay.blogs.web", 20 * 1000);

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STARTED com.liferay.blogs.web", 20 * 1000);

		viewAction.servers.removeModule(tomcat.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void deployModule() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepare(project.getName());

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(tomcat.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		Assert.assertTrue(viewAction.servers.visibleModuleTry(tomcat.getStartedLabel(), project.getName()));

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STARTED " + project.getName() + "_", 20 * 1000);

		viewAction.servers.removeModule(tomcat.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STOPPED " + project.getName() + "_", 20 * 1000);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void deployWar() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(tomcat.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		Assert.assertTrue(viewAction.servers.visibleModuleTry(tomcat.getStartedLabel(), project.getName()));

		jobAction.waitForConsoleContent(
			tomcat.getServerName(), "1 portlet for " + project.getName() + " is available for use", 20 * 1000);

		viewAction.servers.removeModule(tomcat.getStartedLabel(), project.getStartedLabel());

		dialogAction.confirm();

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STOPPED " + project.getName() + "_", 20 * 1000);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}