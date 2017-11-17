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

package com.liferay.ide.ui.hook.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewHookProjectTests extends SwtbotBase {

	@AfterClass
	public static void cleanPluginsSdk() {
		jobAction.waitForCancelIvy();

		viewAction.closeProject(envAction.getLiferayPluginsSdkName());

		jobAction.waitForCloseProject();

		jobAction.waitForNoRunningJobs();

		viewAction.deleteProject(envAction.getLiferayPluginsSdkName());
	}

	@BeforeClass
	public static void createPluginsSdk() throws IOException {
		envAction.unzipPluginsSDK();

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		String projectName = "test-portlet";

		wizardAction.preparePluginSdk(projectName);

		wizardAction.next();

		wizardAction.next();

		String location = envAction.getLiferayPluginsSdkDir().toOSString();

		wizardAction.preparePluginSdkLocation(location);

		wizardAction.finishToWait();

		jobAction.waitForCancelIvy();

		jobAction.waitForCancelValidate(projectName);

		viewAction.closeProject(projectName);

		jobAction.waitForCloseProject();

		viewAction.deleteProject(projectName);
	}

	@Test
	public void createSampleProject() {
		wizardAction.openNewLiferayPluginProjectWizard();

		String projectName = "test-hook";

		wizardAction.preparePluginHookSdk(projectName);

		wizardAction.finishToWait();

		jobAction.waitForCancelIvy();

		jobAction.waitForCancelValidate(projectName);

		viewAction.closeProject(projectName);

		jobAction.waitForCloseProject();

		viewAction.deleteProject(projectName);
	}

}