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

package com.liferay.ide.functional.liferay.support.workspace;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Lily Li
 */
public class LiferayWorkspaceMaven71Support extends LiferayWorkspaceSupport {

	public LiferayWorkspaceMaven71Support(SWTWorkbenchBot bot) {
		super(bot);
	}

	@Override
	public void after() {
		String[] modulesFolderNames = {getName(), getModulesDirName()};

		String[] themesFolderNames = {getName(), getThemesDirName()};

		viewAction.project.openUpdateMavenProjectDialog(getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		viewAction.project.closeAndDeleteFromDisk(modulesFolderNames);

		viewAction.project.closeAndDeleteFromDisk(themesFolderNames);

		super.after();
	}

	@Override
	public void before() {
		super.before();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(getName(), "7.1");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

		viewAction.project.openUpdateMavenProjectDialog(getName());

		dialogAction.updateMavenProject.selectAll();

		dialogAction.confirm();

		jobAction.waitForUpdateMavenProject();

		Assert.assertTrue(viewAction.project.visibleFileTry(getName()));
	}

	public String getModulesDirName() {
		return getName() + "-modules (in modules)";
	}

	public String getThemesDirName() {
		return getName() + "-themes (in themes)";
	}

	public String getWarsDirName() {
		return getName() + "-wars (in wars)";
	}

	@Override
	public void initBundle() {
		viewAction.project.runMavenInitBundle(getName());

		jobAction.waitForNoRunningJobs();
	}

}