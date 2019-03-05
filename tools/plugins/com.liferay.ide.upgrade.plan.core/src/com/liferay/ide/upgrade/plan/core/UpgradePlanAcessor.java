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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

/**
 * @author Gregory Amerson
 */
public interface UpgradePlanAcessor {

	public default UpgradeTaskCategory getCategory(String id) {
		return ServicesLookup.getSingleService(UpgradeTaskCategory.class, "(id=" + id + ")");
	}

	public default UpgradeTaskCategory getCategory(UpgradeTask upgradeTask) {
		return getCategory(upgradeTask.getCategoryId());
	}

	public default UpgradeTaskStep getStep(UpgradeTaskStepAction upgradeTaskStepAction) {
		return ServicesLookup.getSingleService(UpgradeTaskStep.class, "(id=" + upgradeTaskStepAction.getStepId() + ")");
	}

	public default UpgradeTask getTask(UpgradeTaskStep upgradeTaskStep) {
		return ServicesLookup.getSingleService(UpgradeTask.class, "(id=" + upgradeTaskStep.getTaskId() + ")");
	}

}