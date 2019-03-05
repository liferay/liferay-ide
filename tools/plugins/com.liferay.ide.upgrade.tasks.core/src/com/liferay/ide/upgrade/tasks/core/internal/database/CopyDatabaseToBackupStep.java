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

package com.liferay.ide.upgrade.tasks.core.internal.database;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"id=copy_database_to_backup", "requirement=required", "order=1", "taskId=prepare_database",
		"title=Copy Database to Backup",
		"url=https://dev.liferay.com/discover/deployment/-/knowledge_base/7-1/backing-up-a-liferay-installation"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class CopyDatabaseToBackupStep extends BaseUpgradeTaskStep {
}