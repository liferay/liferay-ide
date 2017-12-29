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

import com.liferay.ide.ui.liferay.base.LiferayWorkspaceTomcatGradleBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class NewServiceBuilderModuleLiferayWorkspaceGradleTest extends LiferayWorkspaceTomcatGradleBase {

	@Test
	public void createServiceBuilder() {
		String projectName = "test-sb-in-lrws-gradle";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(projectName, SERVICE_BUILDER);

		wizardAction.finish();

		String[] projectNames = {getLiferayWorkspaceName(), "modules", projectName};

		Assert.assertTrue(viewAction.project.visibleFileTry(projectNames));

		String[] serviceNames = {getLiferayWorkspaceName(), "modules", projectName, projectName + "-service"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceNames));

		String[] apiNames = {getLiferayWorkspaceName(), "modules", projectName, projectName + "-api"};

		Assert.assertTrue(viewAction.project.visibleFileTry(apiNames));

		String[] serviceXmlNames = {getLiferayWorkspaceName(), "modules", projectName, projectName + "-service", "service.xml"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceXmlNames));

		viewAction.project.closeAndDelete(apiNames);
		viewAction.project.closeAndDelete(serviceNames);
		viewAction.project.closeAndDelete(projectNames);
	}

}