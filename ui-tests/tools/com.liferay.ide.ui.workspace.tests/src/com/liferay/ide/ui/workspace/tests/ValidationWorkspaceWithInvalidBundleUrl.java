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
import com.liferay.ide.ui.liferay.support.project.ProjectSupport;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Yuqiang Wang
 * @author Simon Jiang
 */
public class ValidationWorkspaceWithInvalidBundleUrl extends SwtbotBase {

	private static String invalidMessage = "The bundle URL should be a vaild URL.";

	@Test
	public void createLiferayWorkspaceWithDownloadBundleInvalidBundleUrl() {
		String workspaceName = project.getName("test-liferay-workspace-gradle");

		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(workspaceName);

		wizardAction.newLiferayWorkspace.selectDownloadLiferayBundle();

		String bundleHttpsErrorUrl = "https://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleHttpsErrorUrl);

		Assert.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		String bundleHttpErrorUrl = "http://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleHttpErrorUrl);

		Assert.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		String bundleFtpErrorUrl = "ftp://";

		wizardAction.newLiferayWorkspace.setBundleUrl(bundleFtpErrorUrl);

		Assert.assertEquals(invalidMessage, wizardAction.getValidationMsg(4));

		wizardAction.cancel();
	}

	@Rule
	public ProjectSupport project = new ProjectSupport(bot);

}