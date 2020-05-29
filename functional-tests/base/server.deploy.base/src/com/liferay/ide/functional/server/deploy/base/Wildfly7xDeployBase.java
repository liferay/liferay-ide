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

package com.liferay.ide.functional.server.deploy.base;

import com.liferay.ide.functional.liferay.ServerTestBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.server.PureWildfly70Support;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceSupport;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;

/**
 * @author Rui Wang
 */
public abstract class Wildfly7xDeployBase extends ServerTestBase {

	public static PureWildfly70Support wildfly = new PureWildfly70Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningRuleChain(bot, wildfly);

	public void deployModule() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(wildfly.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		Assert.assertTrue(viewAction.servers.visibleModuleTry(wildfly.getStartedLabel(), project.getName()));

		jobAction.waitForConsoleContent(wildfly.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(wildfly.getServerName(), project.getName());

		dialogAction.confirm();

		jobAction.waitForConsoleContent(wildfly.getServerName(), "STOPPED " + project.getName() + "_", M1);

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getModuleFiles(project.getName()));
	}

	public void deployWar() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(wildfly.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		Assert.assertTrue(viewAction.servers.visibleModuleTry(wildfly.getStartedLabel(), project.getName()));

		jobAction.waitForConsoleContent(wildfly.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(wildfly.getServerName(), project.getName());

		dialogAction.confirm();

		jobAction.waitForConsoleContent(wildfly.getServerName(), "STOPPED " + project.getName() + "_", M1);

		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspace().getWarFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	protected abstract LiferayWorkspaceSupport getLiferayWorkspace();

}