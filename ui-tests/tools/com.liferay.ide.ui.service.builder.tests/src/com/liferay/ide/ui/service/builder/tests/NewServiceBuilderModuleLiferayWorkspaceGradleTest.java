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

package com.liferay.ide.ui.service.builder.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class NewServiceBuilderModuleLiferayWorkspaceGradleTest extends SwtbotBase {

	@AfterClass
	public static void cleanLiferayWorkspace() {
		viewAction.closeAndDeleteProject(_lrwsName);

		viewAction.closeAndDeleteProject("modules");
	}

	@BeforeClass
	public static void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceGradle(_lrwsName);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(_lrwsName));
	}

	@Test
	public void createServiceBuilder() {
		String projectName = "test-sb-in-lrws-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleGradle(projectName, SERVICE_BUILDER);

		wizardAction.finish();

		String[] projectNames = {_lrwsName, "modules", projectName};

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectNames));

		String[] serviceNames = {_lrwsName, "modules", projectName, projectName + "-service"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(serviceNames));

		String[] apiNames = {_lrwsName, "modules", projectName, projectName + "-api"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(apiNames));

		String[] serviceXmlNames = {_lrwsName, "modules", projectName, projectName + "-service", "service.xml"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(serviceXmlNames));

		viewAction.closeAndDeleteProject(apiNames);
		viewAction.closeAndDeleteProject(serviceNames);
		viewAction.closeAndDeleteProject(projectNames);
	}

	private static final String _lrwsName = "test-sb-lrws-gradle";

}