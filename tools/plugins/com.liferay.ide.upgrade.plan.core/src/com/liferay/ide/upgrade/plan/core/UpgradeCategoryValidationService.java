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

import com.liferay.ide.core.util.ListUtil;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class UpgradeCategoryValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		final NewUpgradePlanOp op = context(NewUpgradePlanOp.class);

		ElementList<UpgradeCategoryElement> selectedUpgradeCategories = op.getSelectedUpgradeCategories();

		if ((selectedUpgradeCategories == null) || ListUtil.isEmpty(selectedUpgradeCategories)) {
			retval = Status.createErrorStatus("Please choose at lease one upgrade category item.");
		}

		return retval;
	}

}