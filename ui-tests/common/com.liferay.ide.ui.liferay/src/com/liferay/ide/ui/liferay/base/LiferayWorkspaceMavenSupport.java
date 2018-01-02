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

package com.liferay.ide.ui.liferay.base;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import org.junit.Assert;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceMavenSupport extends SupportBase {

	public LiferayWorkspaceMavenSupport(SWTWorkbenchBot bot) {
		super(bot);
	}

	@Override
	public void after() {
		String[] modulesFolderNames = {getLiferayWorkspaceName(), getModulesDirName()};

		String[] themesFolderNames = {getLiferayWorkspaceName(), getThemesDirName()};

		String[] warsFolderNames = {getLiferayWorkspaceName(), getWarsDirName()};

		viewAction.project.closeAndDelete(modulesFolderNames);

		viewAction.project.closeAndDelete(themesFolderNames);

		viewAction.project.closeAndDelete(warsFolderNames);

		viewAction.project.closeAndDelete(getLiferayWorkspaceName());
	}

	@Override
	public void before() {
		super.before();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareMaven(getLiferayWorkspaceName());

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspaceName()));
	}

	public String getLiferayWorkspaceName() {
		return "liferay-workspace-maven-support";
	}

	public String getModulesDirName() {
		return getLiferayWorkspaceName() + "-modules (in modules)";
	}

	public String getThemesDirName() {
		return getLiferayWorkspaceName() + "-themes (in themes)";
	}

	public String getWarsDirName() {
		return getLiferayWorkspaceName() + "-wars (in wars)";
	}

}