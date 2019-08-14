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
public class NewServiceBuilderModuleGradleTest extends SwtbotBase {

	@Test
	public void checkExportPackageChecker() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE_BUILDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openFile(project.getName(), project.getName() + "-api", "bnd.bnd");

		editorAction.serviceXml.switchTabSource();

		validationAction.assertContains("-check: EXPORTS", editorAction.getContent());

		editorAction.close();

		viewAction.gradleTasks.runGradleTask(project.getName(), "build", "build");

		jobAction.waitForConsoleContent(project.getName(), "error  : Exporting an empty package", M1);

		jobAction.waitForConsoleContent(project.getName(), "BUILD FAILED", M1);

		viewAction.console.clickRemoveGradleConsoleBtn();

		viewAction.project.runBuildService(project.getName());

		jobAction.waitForConsoleContent("[Gradle Operations]", "BUILD SUCCESSFUL", M1);

		viewAction.gradleTasks.runGradleTask(project.getName(), project.getName() + "-service", "build", "build");

		jobAction.waitForConsoleContent(project.getName(), "BUILD SUCCESSFUL", M1);

		viewAction.project.closeAndDelete(project.getName(), project.getName() + "-api");
		viewAction.project.closeAndDelete(project.getName(), project.getName() + "-service");
		viewAction.project.closeAndDelete(project.getName());
	}

	@Test
	public void createServiceBuilder() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradle(project.getName(), SERVICE_BUILDER);

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		Assert.assertTrue(viewAction.project.visibleFileTry(project.getName()));

		String[] serviceNames = {project.getName(), project.getName() + "-service"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceNames));

		String[] apiNames = {project.getName(), project.getName() + "-api"};

		Assert.assertTrue(viewAction.project.visibleFileTry(apiNames));

		String[] serviceXmlNames = {project.getName(), project.getName() + "-service", "service.xml"};

		Assert.assertTrue(viewAction.project.visibleFileTry(serviceXmlNames));

		viewAction.project.closeAndDelete(apiNames);
		viewAction.project.closeAndDelete(serviceNames);
		viewAction.project.closeAndDelete(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}