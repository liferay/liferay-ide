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

import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.problems.core.tasks.AutoCorrectUpgradeProblemsStepKeys;
import com.liferay.ide.upgrade.problems.core.tasks.FindUpgradeProblemsStepKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"id=auto_correct_find_upgrade_problems", "order=1", "requirement=required",
		"stepId=" + AutoCorrectUpgradeProblemsStepKeys.ID, "title=" + FindUpgradeProblemsStepKeys.TITLE
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class AutoCorrectFindUpgradeProblemsAction extends FindUpgradeProblemsAction {
}