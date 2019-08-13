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

package com.liferay.ide.functional.jsf.deploy.base;

import com.liferay.ide.functional.liferay.ServerTestBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;

import org.junit.Rule;

/**
 * @author Lily Li
 * @author Ashley Yuan
 */
public class DeployJsfGradleTomcat7xBase extends ServerTestBase {

	public void deployICEFaces() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), ICEFACES);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(server.getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployJSFStandard() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), JSF_STANDARD);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(server.getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployLiferayFacesAlloy() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), LIFERAY_FACES_ALLOY);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(server.getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployPrimeFaces() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), PRIMEFACES);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(server.getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	public void deployRichFaces() {
		wizardAction.openNewLiferayJsfProjectWizard();

		wizardAction.newLiferayJsf.prepareGradle(project.getName(), RICHFACES);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName());

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName());

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName() + "_", M1);

		viewAction.servers.removeModule(server.getServerName(), project.getName());

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}