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

package com.liferay.ide.project.core.descriptor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Version;

/**
 * @author Kuo Zhang
 */
public abstract class UpdateDescriptorVersionOperation implements IDescriptorOperation {

	@Override
	public final IStatus execute(Object... params) {
		if ((params == null) || (params.length != 2)) {
			return Status.OK_STATUS;
		}

		Version v1 = null;
		Version v2 = null;

		if ((params[0] instanceof Version) && (params[1] instanceof Version)) {
			v1 = (Version)params[0];
			v2 = (Version)params[1];
		}
		else if ((params[0] instanceof String) && (params[1] instanceof String)) {
			v1 = new Version((String)params[0]);
			v2 = new Version((String)params[1]);
		}

		return update(v1, v2);
	}

	public abstract IStatus update(Version preVersion, Version postVersion);

}