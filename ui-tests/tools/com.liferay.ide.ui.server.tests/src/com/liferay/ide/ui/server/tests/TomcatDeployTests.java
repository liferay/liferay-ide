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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.base.TomcatRunningSupport;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class TomcatDeployTests extends SwtbotBase {

	@ClassRule
	public static TomcatRunningSupport tomcat = new TomcatRunningSupport(bot);

	@Test
	public void deploySampleProject() {
		wizardAction.openNewLiferayModuleWizard();

		String projectName1 = "test1";

		wizardAction.newModule.prepare(projectName1);

		wizardAction.finish();

		wizardAction.openNewLiferayModuleWizard();

		String projectName2 = "test2";

		wizardAction.newModule.prepare(projectName2);

		wizardAction.finish();

		viewAction.servers.openAddAndRemoveDialog(tomcat.getServerStartedLabel());

		dialogAction.addAndRemove.addModule(projectName1);

		dialogAction.confirm(FINISH);

		jobAction.waitForConsoleContent(tomcat.getServerName(), "STARTED " + projectName1 + "_", 20 * 1000);
	}

}