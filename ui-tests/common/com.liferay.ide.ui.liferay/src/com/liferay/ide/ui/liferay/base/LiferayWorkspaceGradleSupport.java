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
public class LiferayWorkspaceGradleSupport extends LiferayWorkspaceSupport {

	public LiferayWorkspaceGradleSupport(SWTWorkbenchBot bot) {
		super(bot);
	}

	@Override
	public void before() {
		super.before();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(getName());

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(getName()));
	}

	public String getModulesDirName() {
		return "modules";
	}

	public String getThemesDirName() {
		return "themes";
	}

	public String getWarsDirName() {
		return "wars";
	}

}