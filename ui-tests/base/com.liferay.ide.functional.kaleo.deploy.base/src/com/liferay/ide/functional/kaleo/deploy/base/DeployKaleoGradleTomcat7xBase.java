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

package com.liferay.ide.functional.kaleo.deploy.base;

import com.liferay.ide.ui.liferay.ServerTestBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.server.PureTomcatDxp70Support;
import com.liferay.ide.ui.liferay.util.RuleUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class DeployKaleoGradleTomcat7xBase extends ServerTestBase {

	public static PureTomcatDxp70Support tomcat = new PureTomcatDxp70Support(bot);

	@ClassRule
	public static RuleChain chain = RuleUtil.getTomcat7xRuleChain(bot, tomcat);

	public void deployKaleoWorkflowAssignCreatorOnProject() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), MVC_PORTLET);

		wizardAction.finish();

		viewAction.switchKaleoDesignerPerspective();

		wizardAction.openNewLiferayKaleoWorkflowWizard();

		wizardAction.newKaleoWorkflow.openSelectProjectDialog();

		dialogAction.prepareText(project.getName());

		dialogAction.confirm();

		wizardAction.finish();

		viewAction.servers.openEditor(tomcat.getStoppedLabel());

		editorAction.server.setPassword("test");

		editorAction.save();

		editorAction.close();

		viewAction.servers.start(tomcat.getStoppedLabel());

		jobAction.waitForServerStarted(tomcat.getServerName());

		jobAction.waitForNoRunningJobs();

		viewAction.servers.openUpLoadNewWorkflowDialog(tomcat.getStartedLabel());

		dialogAction.workspaceFile.addFiles(project.getName(), "new-workflow-definition.xml");

		dialogAction.confirm();

		viewAction.servers.clickKaleoNameDialog(tomcat.getStartedLabel());

		ide.sleep();

		Assert.assertTrue(
			viewAction.servers.visibleKaleoNameTry(
				tomcat.getStartedLabel(), "New Workflow  [Version: 1, Draft Version: 1]"));

		viewAction.servers.stop(tomcat.getStartedLabel());

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}