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
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.ParentSDKProjectImportOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class SDKImportValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		ParentSDKProjectImportOp op = _op();

		try {
			SDK sdk = SDKUtil.getWorkspaceSDK();

			if (sdk != null) {
				return StatusBridge.create(ProjectCore.createErrorStatus(" This workspace already has another sdk."));
			}

			Path currentProjectLocation = SapphireUtil.getContent(op.getSdkLocation());

			if ((currentProjectLocation != null) && !currentProjectLocation.isEmpty()) {
				sdk = SDKUtil.createSDKFromLocation(PathBridge.create(currentProjectLocation));

				if (sdk != null) {
					IStatus sdkStatus = sdk.validate(true);

					if (!sdkStatus.isOK()) {
						retval = StatusBridge.create(
							ProjectCore.createWarningStatus(sdkStatus.getChildren()[0].getMessage()));
					}
				}
				else {
					retval = StatusBridge.create(
						ProjectCore.createErrorStatus("This parent sdk project path is invalid."));
				}
			}
		}
		catch (CoreException ce) {
		}

		return retval;
	}

	private ParentSDKProjectImportOp _op() {
		return context(ParentSDKProjectImportOp.class);
	}

}