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
		"description=Create new Liferay Workspace that version will be the same as Target Liferay Version selected " +
			"in the Liferay Upgrade Plan.Upgrade Workspace Plugin Version supports update of dependency plugin " +
				"'com.liferay.gradle.plugins.workspace' in settings.gradle."
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class SetupLiferayWorkspaceStep extends BaseUpgradeTaskStep {
}