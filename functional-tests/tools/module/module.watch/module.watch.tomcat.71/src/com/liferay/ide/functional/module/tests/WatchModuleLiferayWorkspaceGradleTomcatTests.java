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

package com.liferay.ide.functional.module.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.server.LiferaryWorkspaceTomcat71Support;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle71Support;
import com.liferay.ide.functional.liferay.util.RuleUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * @author Rui Wang
 */
public class WatchModuleLiferayWorkspaceGradleTomcatTests extends SwtbotBase {

	public static LiferayWorkspaceGradle71Support workspace = new LiferayWorkspaceGradle71Support(bot);
	public static LiferaryWorkspaceTomcat71Support server = new LiferaryWorkspaceTomcat71Support(bot, workspace);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRunningLiferayWokrspaceRuleChain(bot, workspace, server);

	@Test
	public void watchMVCPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(workspace.getName(), "modules", project.getName(), "build.gradle");

		String buildGradleText = editorAction.getContent();

		String previousDependency = "compileOnly group: \"javax.servlet\", name: \"javax.servlet-api\"";

		String newDependency =
			"compileOnly(group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\") { force = true}";

		editorAction.setText(buildGradleText.replace(previousDependency, newDependency));

		editorAction.save();

		editorAction.close();

		viewAction.servers.startWatchingWorkspaceProject(server.getStartedLabel(), workspace.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModuleFromPortal(
			server.getStartedLabel(), workspace.getName() + " [watching]", project.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + "_", M1);

		viewAction.servers.stopWatchingWorkspaceProject(server.getStartedLabel(), workspace.getName() + " [watching]");

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDeleteFromDisk(workspace.getModuleFiles(project.getName()));

		Assert.assertFalse(viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName()));
	}

	@Test
	public void watchServiceBuilder() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), SERVICE_BUILDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.runBuildService(workspace.getName(), "modules", project.getName());

		jobAction.waitForNoRunningJobs();

		viewAction.servers.startWatchingWorkspaceProject(server.getStartedLabel(), workspace.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + ".api_", M2);

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + ".service_", M2);

		viewAction.servers.refreshWatchProject(server.getStartedLabel(), workspace.getName() + " [watching]");

		ide.sleep();

		Assert.assertTrue(
			viewAction.servers.visibleWorkspaceTry(
				server.getStartedLabel(), workspace.getName() + " [watching]", project.getName() + "-api [Active]"));

		Assert.assertTrue(
			viewAction.servers.visibleWorkspaceTry(
				server.getStartedLabel(), workspace.getName() + " [watching]",
				project.getName() + "-service [Active]"));

		viewAction.servers.stopWatchingWorkspaceProject(server.getStartedLabel(), workspace.getName() + " [watching]");

		jobAction.waitForNoRunningJobs();

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + ".api_", M2);

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + ".service_", M2);

		viewAction.project.closeAndDeleteFromDisk(
			workspace.getModuleFiles(project.getName(), project.getName() + "-service"));

		viewAction.project.closeAndDeleteFromDisk(
			workspace.getModuleFiles(project.getName(), project.getName() + "-api"));

		jobAction.waitForNoRunningJobs();

		viewAction.project.closeAndDeleteFromDisk(workspace.getModuleFiles(project.getName()));
	}

	@Test
	public void watchWarMvcPortlet() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), WAR_MVC_PORTLET);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(workspace.getName());

		viewAction.servers.startWatchingProject(server.getStartedLabel(), workspace.getName(), project.getName());

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STARTED " + project.getName() + "_", M5);

		viewAction.servers.stopWatchingProject(
			server.getStartedLabel(), workspace.getName(), project.getName() + " [watching]");

		jobAction.waitForConsoleContent(
			server.getServerName() + " [Liferay 7.x]", "STOPPED " + project.getName() + "_", M5);

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.closeAndDeleteFromDisk(workspace.getWarFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}