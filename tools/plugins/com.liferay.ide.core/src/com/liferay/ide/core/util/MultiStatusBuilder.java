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

package com.liferay.ide.core.util;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Gregory Amerson
 */
public class MultiStatusBuilder {

	public MultiStatusBuilder(String pluginId) {
		_pluginId = pluginId;

		_retval = new MultiStatus(pluginId, 0, null, null);
	}

	public MultiStatusBuilder add(IStatus status) {
		_retval.add(status);

		return this;
	}

	public MultiStatusBuilder addAll(List<Throwable> exceptions) {
		for (Throwable t : exceptions) {
			Status status = new Status(IStatus.ERROR, _pluginId, t.getMessage(), t);

			_retval.add(status);
		}

		return this;
	}

	public IStatus build() {
		return _retval;
	}

	private final String _pluginId;
	private final MultiStatus _retval;

}