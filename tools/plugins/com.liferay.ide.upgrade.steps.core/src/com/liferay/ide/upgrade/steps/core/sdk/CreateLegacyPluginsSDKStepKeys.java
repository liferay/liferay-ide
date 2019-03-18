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

package com.liferay.ide.upgrade.steps.core.sdk;

/**
 * @author Gregory Amerson
 */
public class CreateLegacyPluginsSDKStepKeys {

	public static final String DESCRIPTION =
		"<p>For any plugins that you wish to just adapt to new APIs but continue to build in the traditional way " +
		"(i.e., as a Plugins SDK ant-style project), we will need to create a compatibility Plugins SDK folder " +
		"inside your new workspace. This new folder is called \"plugins-sdk\" by default.</p>";

	public static final String ID = "create_legacy_plugins_sdk";

	public static final String TITLE = "Create Legacy Plugins SDK";

}