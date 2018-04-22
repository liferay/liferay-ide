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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

/**
 * @author Gregory Amerson
 */
public class SDKProjectVariableResolver implements IDynamicVariableResolver {

	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
		String retval = null;
		SDK sdk = null;

		if (CoreUtil.isNullOrEmpty(argument)) {
			sdk = SDKManager.getInstance().getDefaultSDK();
		}
		else {
			sdk = SDKUtil.getSDK(CoreUtil.getProject(argument));
		}

		if (sdk != null) {
			retval = sdk.getLocation().toOSString();
		}

		return retval;
	}

}