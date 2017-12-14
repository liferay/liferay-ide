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

package com.liferay.ide.ui.liferay;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Terry Jia
 */
public class SdkBase extends SwtbotBase {

	@AfterClass
	public static void cleanPluginsSdk() {
		jobAction.waitForIvy();

		String sdkName = envAction.getSdkName() + "-" + envAction.getTimestamp();

		viewAction.project.closeAndDeleteWithNoRunningJobs(sdkName);
	}

	@BeforeClass
	public static void createPluginsSdk() throws IOException {
		envAction.unzipPluginsSdk();

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		String projectName = "test-portlet";

		wizardAction.newPlugin.prepareSdk(projectName);

		wizardAction.next();

		wizardAction.next();

		String location = envAction.getSdkDir().toOSString();

		wizardAction.setSdkLocation.prepare(location);

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDelete(projectName);
	}

}