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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.workspace.LiferayWorkspaceMavenSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joye Luo
 * @author Ying Xu
 */
public class NewServiceBuilderModuleLiferayWorkspaceMavenTest extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceMavenSupport liferayWorkspace = new LiferayWorkspaceMavenSupport(bot);

	@Test
	public void createServiceBuilder() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareMaven(project.getName(), SERVICE_BUILDER);

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getModuleFiles(project.getName())));

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getModuleFiles(project.getName(), project.getName() + "-service")));

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getModuleFiles(project.getName(), project.getName() + "-api")));

		Assert.assertTrue(
			viewAction.project.visibleFileTry(
				liferayWorkspace.getModuleFiles(project.getName(), project.getName() + "-service", "service.xml")));

		viewAction.project.closeAndDelete(
			liferayWorkspace.getModuleFiles(project.getName(), project.getName() + "-api"));
		viewAction.project.closeAndDelete(
			liferayWorkspace.getModuleFiles(project.getName(), project.getName() + "-service"));
		viewAction.project.closeAndDelete(liferayWorkspace.getModuleFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}