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

package com.liferay.ide.ui.module.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.TomcatRunningSupport;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class DeployModuleMavenTomcatTests extends SwtbotBase {

	@ClassRule
	public static TomcatRunningSupport tomcat = new TomcatRunningSupport(bot);

	@Test
	public void deployActivator() {
		String projectName = "deploy-activator-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, ACTIVATOR);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(tomcat.getServerStartedLabel());

		dialogAction.addAndRemove.addModule(projectName);

		dialogAction.confirm(FINISH);

		jobAction.waitForConsoleContent(
			tomcat.getServerName(), "STARTED " + projectName.replace('-', '.') + "_", 20 * 1000);

		viewAction.project.closeAndDelete(projectName);
	}

}
