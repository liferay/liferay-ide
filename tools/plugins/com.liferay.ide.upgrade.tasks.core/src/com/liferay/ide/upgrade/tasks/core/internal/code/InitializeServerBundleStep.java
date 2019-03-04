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

package com.liferay.ide.upgrade.tasks.core.internal.code;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"id=initialize_server_bundle", "imagePath=icons/server.gif", "requirement=recommended", "order=4",
		"taskId=setup_development_environment", "title=Initialize Server Bundle",
		"description=Select the Liferay Workspace set in the previous step to initialize Liferay Server Bundle.The " +
			"version of Liferay Server Bundle is determined by the setting of gradle.properties in Liferay Workspace."
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class InitializeServerBundleStep extends BaseUpgradeTaskStep {
}