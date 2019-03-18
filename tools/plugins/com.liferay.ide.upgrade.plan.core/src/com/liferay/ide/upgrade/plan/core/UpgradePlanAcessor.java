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
 * @author Simon Jiang
 * @author Terry Jia
 */
public interface UpgradePlanAcessor {

	public default UpgradeStepCategory getCategory(String id) {
		return ServicesLookup.getSingleService(UpgradeStepCategory.class, "(id=" + id + ")");
	}

	public default UpgradeStepCategory getCategory(UpgradeStep step) {
		return getCategory(step.getCategoryId());
	}

	public default UpgradeStep getStep(String id) {
		return ServicesLookup.getSingleService(UpgradeStep.class, "(id=" + id + ")");
	}

}