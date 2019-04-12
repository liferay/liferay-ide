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
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;
import com.liferay.ide.upgrade.plan.core.SDKSupport;
import com.liferay.ide.upgrade.plan.core.WorkspaceSupport;

import java.io.File;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class SourceLocationValidationService
	extends ValidationService implements SapphireContentAccessor, SDKSupport, WorkspaceSupport {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewUpgradePlanOp op = context(NewUpgradePlanOp.class);

		Path sourceLocation = get(op.getLocation());

		if (sourceLocation != null) {
			File sourceLocationTarget = sourceLocation.toFile();

			if (!(sourceLocationTarget.exists() && sourceLocationTarget.canRead())) {
				retval = Status.createErrorStatus("Source code location must exist and be readable.");
			}

			if (!(isValidGradleWorkspace(sourceLocationTarget) || isValidSDK(sourceLocationTarget))) {
				retval = Status.createErrorStatus("Source code location must be a vaild gradle liferay workspace.");
			}
		}

		return retval;
	}

}