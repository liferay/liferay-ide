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

package com.liferay.ide.ui.workspace.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferayWorkspaceWizardMavenTests extends SwtbotBase {

	@Test
	public void createLiferayWorksapce() {
		String workspaceName = "test-liferay-workspace-maven";

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(workspaceName);

		wizardAction.finish();

		String[] moduleNames = {workspaceName, "test-liferay-workspace-maven-modules (in modules)"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(moduleNames));

		String[] themeNames = {workspaceName, "test-liferay-workspace-maven-themes (in themes)"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(themeNames));

		String[] warNames = {workspaceName, "test-liferay-workspace-maven-wars (in wars)"};

		Assert.assertTrue(viewAction.visibleProjectFileTry(warNames));

		viewAction.closeAndDeleteProject(moduleNames);
		viewAction.closeAndDeleteProject(themeNames);
		viewAction.closeAndDeleteProject(warNames);

		viewAction.closeAndDeleteProject(workspaceName);
	}

	@Test
	public void createLiferayWorksapceChangeLocation() {
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundle() {
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundleChangeBundleUrl() {
	}

	@Test
	public void createLiferayWorksapceWithDownloadBundleChangeServerName() {
	}

}