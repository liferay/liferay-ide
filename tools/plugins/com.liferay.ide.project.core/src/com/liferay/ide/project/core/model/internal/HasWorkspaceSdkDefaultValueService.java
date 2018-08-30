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

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Simon Jiang
 */
public class HasWorkspaceSdkDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		try {
			SDK sdk = SDKUtil.getWorkspaceSDK();

			if (sdk != null) {
				IStatus status = sdk.validate();

				if (status.isOK()) {
					return "true";
				}
			}
		}
		catch (CoreException ce) {
		}

		return "false";
	}

}