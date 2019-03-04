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

package com.liferay.ide.upgrade.tasks.core.internal.sdk;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=initialize_latest_plugins_sdk", "imagePath=icons/new.png", "requirement=required", "order=0",
		"taskId=migrate_plugins_sdk", "title=Initialize the Latest Plugins SDK",
		"description=Performing this step, it will download the latest sdk and extract into plugins-sdk folder in your liferay workspace project."
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class InitializeLatestPluginsSDKStep extends BaseUpgradeTaskStep {
}