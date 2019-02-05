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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=auto_correct_problems", "imagePath=icons/auto_correct_problems.png", "requirement=recommended", "order=200",
		"taskId=find_upgrade_problems", "title=Auto Correct Upgrade Problems"
	},
	service = UpgradeTaskStep.class
)
public class AutoCorrectUpgradeProblemsTaskStep extends BaseUpgradeTaskStep {
}