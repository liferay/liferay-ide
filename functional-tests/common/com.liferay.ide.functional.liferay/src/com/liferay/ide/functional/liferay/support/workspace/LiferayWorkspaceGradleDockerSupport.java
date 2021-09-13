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
public class LiferayWorkspaceGradleDockerSupport extends LiferayWorkspaceSupport {

	public LiferayWorkspaceGradleDockerSupport(SWTWorkbenchBot bot) {
		super(bot);
	}

	@Override
	public void before() {
		super.before();

		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openGradleTry();

		dialogAction.gradle.checkAutomaticSync();

		dialogAction.preferences.confirm();

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(getName(), "portal-7.4-ga3");

		wizardAction.finish();

		jobAction.waitForNoRunningJobs();

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

	@Override
	public void initBundle() {
		viewAction.project.runInitPortalDockerBundle(getName());

		jobAction.waitForNoRunningJobs();
	}

}