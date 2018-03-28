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

package com.liferay.ide.ui.module.deploy.tomcat70.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.server.PureTomcat70Support;
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
public class DeployModuleMavenTomcatTests extends SwtbotBase {

	public static PureTomcat70Support tomcat = new PureTomcat70Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, tomcat);

	@Test
	public void deployActivator() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), ACTIVATOR);

		wizardAction.finish();

		viewAction.project.openUpdateMavenProjectDialog(project.getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		viewAction.servers.openAddAndRemoveDialog(tomcat.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(tomcat.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STARTED " + project.getName() + "_", 60 * 1000);

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}