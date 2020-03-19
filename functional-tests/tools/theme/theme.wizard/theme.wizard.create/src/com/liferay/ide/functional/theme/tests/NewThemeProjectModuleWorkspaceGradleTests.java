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

package com.liferay.ide.functional.theme.tests;

import com.liferay.ide.functional.liferay.SwtbotBase;
import com.liferay.ide.functional.liferay.support.project.ProjectSupport;
import com.liferay.ide.functional.liferay.support.workspace.LiferayWorkspaceGradle71Support;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Rui Wang
 */
public class NewThemeProjectModuleWorkspaceGradleTests extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradle71Support liferayWorkspace = new LiferayWorkspaceGradle71Support(bot);

	@Test
	public void createTheme() {
		wizardAction.openNewLiferayModuleWizard();

		wizardAction.newModule.prepareGradleInWorkspace(project.getName(), THEME);

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.refreshGradleProject(liferayWorkspace.getName());

		Assert.assertTrue(viewAction.project.visibleFileTry(liferayWorkspace.getWarFiles(project.getName())));

		viewAction.project.closeAndDelete(liferayWorkspace.getWarFiles(project.getName()));
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}