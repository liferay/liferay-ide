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
		"id=setup_liferay_workspace", "imagePath=icons/liferay_new.png", "requirement=required", "order=2",
		"taskId=setup_development_environment", "title=Setup Liferay Workspace",
		"description=Create a new Liferay Workspace with the same version as Target Liferay Version which you selected in new Liferay Upgrade Plan wizard. Your liferay workspace version will be updated to the version that has been set in settings.gralde which resides in your Workspace’s root folder"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class SetupLiferayWorkspaceStep extends BaseUpgradeTaskStep {
}