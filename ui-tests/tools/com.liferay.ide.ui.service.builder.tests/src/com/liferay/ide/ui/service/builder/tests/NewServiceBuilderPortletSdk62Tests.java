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

package com.liferay.ide.ui.service.builder.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.ProjectSupport;
import com.liferay.ide.ui.liferay.base.Sdk62Support;
import com.liferay.ide.ui.liferay.base.Tomcat62Support;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
@Ignore("there are a few sub process still running after Refresh server adapter, need more research")
public class NewServiceBuilderPortletSdk62Tests extends SwtbotBase {

	public static Tomcat62Support tomcat62 = new Tomcat62Support(bot);

	@ClassRule
	public static RuleChain chain = RuleChain.outerRule(tomcat62).around(new Sdk62Support(bot, tomcat62));

	@Ignore("ignore as service builder in sdk62 is only able run in java 7")
	@Test
	public void buildServiceOnProject() {
		if (envAction.notInternal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareServiceBuilderPortletSdk(project.getName());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getName());

		viewAction.project.runBuildServices(project.getName());

		jobAction.waitForConsoleContent("build.xml", "BUILD SUCCESSFUL", 30 * 1000);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void buildWSDDOnProject() {
		if (envAction.notInternal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareServiceBuilderPortletSdk(project.getName());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getName());

		viewAction.project.runBuildWSDD(project.getName());

		jobAction.waitForConsoleContent("build.xml", "BUILD SUCCESSFUL", 300 * 1000);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}