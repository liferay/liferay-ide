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

package com.liferay.ide.functional.service.builder.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joye Luo
 * @author Ying Xu
 */
public class NewServiceBuilderModuleMavenTest extends SwtbotBase {

	@Test
	public void createServiceBuilder() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), SERVICE_BUILDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		String[] serviceNames = {project.getName(), project.getName() + "-service"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceNames));

		String[] apiNames = {project.getName(), project.getName() + "-api"};

		Assert.assertTrue(viewAction.project.visibleFileTry(apiNames));

		String[] serviceXmlNames = {project.getName(), project.getName() + "-service", "service.xml"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceXmlNames));

		viewAction.project.closeAndDeleteWithNoRunningJobs(apiNames);
		viewAction.project.closeAndDeleteWithNoRunningJobs(serviceNames);
		viewAction.project.closeAndDeleteWithNoRunningJobs(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}