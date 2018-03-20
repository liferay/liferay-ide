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

package com.liferay.ide.ui.hook.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.server.PureTomcat62Support;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 */
@Ignore("ignore as it may fails but unstable happen and need more research")
public class NewHookProjectSdk62Tests extends SwtbotBase {

	public static PureTomcat62Support tomcat62 = new PureTomcat62Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat62SdkRuleChain(bot, tomcat62);

	@Test
	public void createSampleProject() {
		if (envAction.notInternal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareHookSdk(project.getName());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getName());

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}