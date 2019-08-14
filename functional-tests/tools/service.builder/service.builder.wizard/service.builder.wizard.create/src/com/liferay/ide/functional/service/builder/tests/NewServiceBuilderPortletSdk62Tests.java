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

package com.liferay.ide.functional.service.builder.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.SdkProjectSupport;
import com.liferay.ide.functional.liferay.support.server.PureTomcat62Support;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Joye Luo
 * @author Terry Jia
 * @author Ying Xu
 */
@Ignore("there are a few sub process still running after Refresh server adapter, need more research")
public class NewServiceBuilderPortletSdk62Tests extends SwtbotBase {

	public static PureTomcat62Support tomcat62 = new PureTomcat62Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat62SdkRuleChain(bot, tomcat62);

	@Ignore("ignore as service builder in sdk62 is only able run in java 7")
	@Test
	public void buildServiceOnProject() {
		if (envAction.notInternal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareServiceBuilderPortletSdk(project.getNamePortlet());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getNamePortlet());

		viewAction.project.runBuildServices(project.getNamePortlet());

		jobAction.waitForConsoleContent("build.xml", "BUILD SUCCESSFUL", 30 * 1000);

		viewAction.project.closeAndDelete(project.getNamePortlet());
	}

	@Test
	public void buildWSDDOnProject() {
		if (envAction.notInternal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareServiceBuilderPortletSdk(project.getNamePortlet());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getNamePortlet());

		viewAction.project.runBuildWSDD(project.getNamePortlet());

		jobAction.waitForConsoleContent("build.xml", "BUILD SUCCESSFUL", 300 * 1000);

		viewAction.project.closeAndDelete(project.getNamePortlet());
	}

	@Rule
	public SdkProjectSupport project = new SdkProjectSupport(bot);

}