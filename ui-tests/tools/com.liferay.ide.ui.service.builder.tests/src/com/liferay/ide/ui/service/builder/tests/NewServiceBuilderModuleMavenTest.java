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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class NewServiceBuilderModuleMavenTest extends SwtbotBase {

	@Test
	public void createServiceBuilder() {
		String projectName = "test-sb-maven";

		wizardAction.openNewLiferayModuleWizard();

		wizardAction.prepareLiferayModuleMaven(projectName, SERVICE_BUILDER);

		wizardAction.finish();

		Assert.assertTrue(viewAction.visibleProjectFileTry(projectName));

		String[] serviceNames = { projectName, projectName + "-service" };

		Assert.assertTrue(viewAction.visibleProjectFileTry(serviceNames));

		String[] apiNames = { projectName, projectName + "-api" };

		Assert.assertTrue(viewAction.visibleProjectFileTry(apiNames));

		String[] serviceXmlNames = { projectName, projectName + "-service", "service.xml" };

		Assert.assertTrue(viewAction.visibleProjectFileTry(serviceXmlNames));

		viewAction.closeAndDeleteProject(apiNames);
		viewAction.closeAndDeleteProject(serviceNames);
		viewAction.closeAndDeleteProject(projectName);
	}

}