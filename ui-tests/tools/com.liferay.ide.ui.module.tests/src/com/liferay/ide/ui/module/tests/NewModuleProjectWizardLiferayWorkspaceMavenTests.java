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

package com.liferay.ide.ui.module.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewModuleProjectWizardLiferayWorkspaceMavenTests extends SwtbotBase {

	@AfterClass
	public static void cleanLiferayWorkspace() {
		viewAction.deleteProject(_liferayWorkspaceName);
	}

	@BeforeClass
	public static void createLiferayWorkspace() {
		wizardAction.openNewLiferayWorkspaceWizard();

		wizardAction.prepareLiferayWorkspaceMaven(_liferayWorkspaceName);

		wizardAction.finishToWait();

		Assert.assertTrue(viewAction.visibleProjectFileTry(_liferayWorkspaceName));
	}

	@Test
	public void createActivator() {
	}

	private static final String _liferayWorkspaceName = "test-liferay-workspace-maven";

}