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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceTomcatGradleBase extends TomcatBase {

	@AfterClass
	public static void cleanLiferayWorkspace() {
		viewAction.project.closeAndDeleteFromDisk(getLiferayWorkspaceName());
	}

	public static String getLiferayWorkspaceName() {
		return "test-liferay-workspace-gradle";
	}

	public static String getServerName() {
		return "test-tomcat-gradle";
	}

	@BeforeClass
	public static void prepareLiferayWorkspace() throws IOException {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.newLiferayWorkspace.prepareGradle(getLiferayWorkspaceName());

		wizardAction.finish();

		Assert.assertTrue(viewAction.project.visibleFileTry(getLiferayWorkspaceName()));
	}

}