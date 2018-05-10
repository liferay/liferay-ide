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

package com.liferay.ide.ui.service.builder.deploy.base;

import com.liferay.ide.ui.liferay.ServerTestBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;

import org.junit.Rule;

/**
 * @author Ying Xu
 */
public class DeployServiceBuilderModuleGradleTomcat7xBase extends ServerTestBase {

	public void deployServiceBuilder() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE_BUILDER);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(server.getStartedLabel());

		dialogAction.addAndRemove.addModule(project.getName("-api"));

		dialogAction.addAndRemove.addModule(project.getName("-service"));

		dialogAction.confirm(FINISH);

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName("-api"));

		viewAction.servers.visibleModuleTry(server.getStartedLabel(), project.getName("-service"));

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName(".api") + "_", S20);

		jobAction.waitForConsoleContent(server.getServerName(), "STARTED " + project.getName(".service") + "_", S20);

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel("-api"));

		dialogAction.confirm();

		viewAction.servers.removeModule(server.getStartedLabel(), project.getStartedLabel("-service"));

		dialogAction.confirm();

		viewAction.project.closeAndDelete(project.getName());
		viewAction.project.closeAndDelete(project.getName() + "-api");
		viewAction.project.closeAndDelete(project.getName() + "-service");
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}