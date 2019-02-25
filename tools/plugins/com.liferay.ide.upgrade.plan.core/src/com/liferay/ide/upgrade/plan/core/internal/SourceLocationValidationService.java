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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;
import com.liferay.ide.upgrade.plan.core.UpgradeCategoryElement;

import java.io.File;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class SourceLocationValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewUpgradePlanOp op = context(NewUpgradePlanOp.class);

		ElementList<UpgradeCategoryElement> selectedCategories = op.getUpgradeCategories();

		if (ListUtil.isNotEmpty(selectedCategories)) {
			Path sourceLocation = get(op.getLocation());

			if ((sourceLocation == null) || sourceLocation.isEmpty() || !sourceLocation.isAbsolute()) {
				retval = Status.createErrorStatus("Plese select correct source code location.");
			}
			else {
				File sourceLocationTarget = sourceLocation.toFile();

				if (!(sourceLocationTarget.exists() && sourceLocationTarget.canRead())) {
					retval = Status.createErrorStatus("Source location can not be found or read.");
				}
			}
		}

		return retval;
	}

}