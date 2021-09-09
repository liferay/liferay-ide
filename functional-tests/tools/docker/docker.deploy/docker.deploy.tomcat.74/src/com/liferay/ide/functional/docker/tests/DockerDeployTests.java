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

package com.liferay.ide.functional.docker.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.server.LiferaryWorkspaceDockerSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradleSupportForDocker;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Lily Li
 */
public class DockerDeployTests extends SwtbotBase {

	public static LiferayWorkspaceGradleSupportForDocker workspace = new LiferayWorkspaceGradleSupportForDocker(bot);
	public static LiferaryWorkspaceDockerSupport server = new LiferaryWorkspaceDockerSupport(bot, workspace);

	@ClassRule
	public static RuleChain chain = RuleUtil.getDockerRunningLiferayWorkspaceRuleChain(bot, workspace, server);

	@Test
	public void deployModule() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(workspace.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.project.runDockerDeploy(workspace.getModuleFiles(project.getName()));

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.project.closeAndDeleteFromDisk(workspace.getModuleFiles(project.getName()));
	}

	@Test
	public void deployWar() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(workspace.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.project.runDockerDeploy(workspace.getModuleFiles(project.getName()));

		jobAction.waitForConsoleContent(getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.project.closeAndDeleteFromDisk(workspace.getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

	protected String getServerName() {
		return server.getServerName();
	}

}