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
public class NewServiceBuilderModuleLiferayWorkspaceMavenTest extends SwtbotBase {

	@AfterClass
	public static void cleanLiferayWorkspace() {
		String[] moduleNames = {_lrwsName, _lrwsName + "-modules (in modules)"};
		String[] themeNames = {_lrwsName, _lrwsName + "-themes (in themes)"};
		String[] warNames = {_lrwsName, _lrwsName + "-wars (in wars)"};

		viewAction.project.closeAndDelete(moduleNames);
		viewAction.project.closeAndDelete(themeNames);
		viewAction.project.closeAndDelete(warNames);
		viewAction.project.closeAndDelete(_lrwsName);
	}

	@BeforeClass
	public static void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(_lrwsName);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(_lrwsName));
	}

	@Test
	public void createServiceBuilder() {
		String projectName = "test-sb-in-lws-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(projectName, SERVICE_BUILDER);

		wizardAction.finish();

		String[] projectNames = {_lrwsName, _lrwsName + "-modules (in modules)", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		String[] serviceNames = {_lrwsName, _lrwsName + "-modules (in modules)", projectName, projectName + "-service"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceNames));

		String[] apiNames = {_lrwsName, _lrwsName + "-modules (in modules)", projectName, projectName + "-api"};

		Assert.assertTrue(viewAction.project.visibleFileTry(apiNames));

		String[] serviceXmlNames =
			{_lrwsName, _lrwsName + "-modules (in modules)", projectName, projectName + "-service", "service.xml"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceXmlNames));

		viewAction.project.closeAndDelete(apiNames);
		viewAction.project.closeAndDelete(serviceNames);
		viewAction.project.closeAndDelete(projectNames);
	}

	private static final String _lrwsName = "test-sb-lrws-maven";

}