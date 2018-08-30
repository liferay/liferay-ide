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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.model.SDKProjectsImportOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class SDKImportLocationValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		SDKProjectsImportOp op = _op();

		Path currentProjectLocation = SapphireUtil.getContent(op.getSdkLocation());

		if ((currentProjectLocation != null) && !currentProjectLocation.isEmpty()) {
			String currentPath = currentProjectLocation.toOSString();

			retval = StatusBridge.create(ProjectImportUtil.validateSDKPath(currentPath));
		}

		return retval;
	}

	private SDKProjectsImportOp _op() {
		return context(SDKProjectsImportOp.class);
	}

}