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

package com.liferay.ide.upgrade.problems.core.internal.steps;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeStep;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.problems.core.steps.FindUpgradeProblemsStepKeys;
import com.liferay.ide.upgrade.problems.core.steps.FixUpgradeProblemsStepKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"description=" + FindUpgradeProblemsStepKeys.DESCRIPTION, "id=" + FindUpgradeProblemsStepKeys.ID,
		"imagePath=icons/find_upgrade_problems.gif", "requirement=recommended", "order=2",
		"title=" + FindUpgradeProblemsStepKeys.TITLE, "parentId=" + FixUpgradeProblemsStepKeys.ID
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeStep.class
)
public class FindUpgradeProblemsStep extends BaseUpgradeStep {
}