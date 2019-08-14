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

package com.liferay.ide.functional.liferay.support.sdk;

import com.liferay.ide.functional.liferay.support.server.ServerSupport;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class SdkSupport extends SdkSupportBase {

	public SdkSupport(SWTWorkbenchBot bot, ServerSupport server) {
		super(bot, "1.0.16", server);
	}

	public SdkSupport(SWTWorkbenchBot bot, String version, ServerSupport server) {
		super(bot, version, server);
	}

	@Override
	public void after() {
		jobAction.waitForIvy();

		// In fact we need to wait our jobs done

		// but now it takes too long as the fetch the server list job

		// viewAction.project.closeAndDeleteWithNoRunningJobs(sdkName);

		viewAction.project.closeAndDelete(getSdkDirName());

		super.after();
	}

	@Override
	public void before() {
		super.before();

		viewAction.switchLiferayPerspective();

		wizardAction.openNewLiferayPluginProjectWizard();

		String projectName = "test-portlet";

		wizardAction.newPlugin.prepareSdk(projectName);

		wizardAction.next();

		wizardAction.next();

		wizardAction.setSdkLocation.prepare(getFullSdkDir());

		wizardAction.finish();

		jobAction.waitForIvy();

		jobAction.waitForValidate(projectName);

		viewAction.project.closeAndDelete(projectName);
	}

}