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

package com.liferay.ide.project.core.upgrade.service;

import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Terry Jia
 */
public class SdkLocationDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String retVal = "";

		try {
			IProject sdk = SDKUtil.getWorkspaceSDKProject();

			if (sdk != null) {
				IPath location = sdk.getLocation();

				retVal = location.toString();
			}
		}
		catch (CoreException ce) {
		}

		return retVal;
	}

}