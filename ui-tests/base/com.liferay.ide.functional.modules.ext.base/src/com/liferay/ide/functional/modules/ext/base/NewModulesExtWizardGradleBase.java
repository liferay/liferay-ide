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

package com.liferay.ide.functional.modules.ext.base;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;
import com.liferay.ide.ui.liferay.support.workspace.LiferayWorkspaceGradleSupport;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Rui Wang
 */
public class NewModulesExtWizardGradleBase extends SwtbotBase {

	@ClassRule
	public static LiferayWorkspaceGradleSupport liferayWorkspace = new LiferayWorkspaceGradleSupport(bot);

	public void addModulesExt() {
		wizardAction.openNewLiferayModulesExtWizard();

		wizardAction.newModulesExt.prepare(project.getName());

		wizardAction.newModulesExt.openSelectBrowseDialog();

		dialogAction.prepareText("com.liferay.login.web");

		dialogAction.confirm();

		wizardAction.finish();

		jobAction.waitForNoRunningProjectBuildingJobs();

		viewAction.project.openFile(liferayWorkspace.getName(), "ext", project.getName(), "build.gradle");

		Assert.assertTrue(
			viewAction.project.visibleFileTry(liferayWorkspace.getName(), "ext", project.getName(), "build.gradle"));

		editorAction.close();

		viewAction.project.closeAndDelete(liferayWorkspace.getName(), "ext");
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}