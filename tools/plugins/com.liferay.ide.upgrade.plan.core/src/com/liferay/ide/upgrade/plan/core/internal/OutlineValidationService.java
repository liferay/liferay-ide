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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.IUpgradePlanOutline;
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;

import org.apache.commons.validator.routines.UrlValidator;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class OutlineValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewUpgradePlanOp op = context(NewUpgradePlanOp.class);

		IUpgradePlanOutline upgradePlanOutline = get(op.getUpgradePlanOutline());

		if ((upgradePlanOutline != null) && !upgradePlanOutline.isOffline()) {
			UrlValidator urlValidator = new UrlValidator();

			boolean validStatus = urlValidator.isValid(upgradePlanOutline.getLocation());

			if (!validStatus) {
				return Status.createErrorStatus(upgradePlanOutline.getName() + " is not a vaild url.");
			}
		}

		return retval;
	}

}