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

package com.liferay.ide.functional.layouttpl.tests;

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
@Ignore("ignore for more research")
public class NewLayouttplProjectSdk62Tests extends SwtbotBase {

	public static PureTomcat62Support tomcat62 = new PureTomcat62Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat62SdkRuleChain(bot, tomcat62);

	@Test
	public void createLayoutTemplate() {
		if (!envAction.internal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareLayoutTemplateSdk(project.getName());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getName());

		wizardAction.openNewLiferayLayoutTemplate();

		wizardAction.finish();

		String layoutTpl = "newtemplate.tpl";

		viewAction.project.openFile(project.getName() + "-layouttpl", "docroot", layoutTpl);

		editorAction.close();

		String layoutWapTpl = "newtemplate.wap.tpl";

		viewAction.project.openFile(project.getName() + "-layouttpl", "docroot", layoutWapTpl);

		editorAction.close();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createLayoutTemplateProject() {
		if (!envAction.internal()) {
			return;
		}

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		wizardAction.newPlugin.prepareLayoutTemplateSdk(project.getName());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(project.getName());

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}