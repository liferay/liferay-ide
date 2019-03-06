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

package com.liferay.ide.upgrade.tasks.core.internal.prerequisite;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Christopher Bryan Boyd
 */
@Component(
	property = {
		"categoryId=code", "description=" + PrepareForCodeUpgradeTaskKeys.DESCRIPTION,
		"id=" + PrepareForCodeUpgradeTaskKeys.ID, "imagePath=icons/setup.png", "order=1",
		"title=" + PrepareForCodeUpgradeTaskKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTask.class
)
public class PrepareForCodeUpgradeTask extends BaseUpgradeTask {
}