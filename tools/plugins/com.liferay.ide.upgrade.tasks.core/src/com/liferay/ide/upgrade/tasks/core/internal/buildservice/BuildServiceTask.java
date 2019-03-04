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

package com.liferay.ide.upgrade.tasks.core.internal.buildservice;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = {
		"categoryId=code", "id=build_service", "imagePath=icons/build_service.png", "order=6", "title=Build Service",
		"description=Building service generates your project’s persistence layer by automating the creation of interfaces and classes according to your configurations."
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTask.class
)
public class BuildServiceTask extends BaseUpgradeTask {
}