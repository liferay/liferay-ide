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

package com.liferay.ide.server.core.portal;

import org.eclipse.core.runtime.IStatus;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BundleDTOWithStatus extends BundleDTO {

	public BundleDTOWithStatus(BundleDTO original, IStatus status) {
		id = original.id;
		lastModified = original.lastModified;
		state = original.state;
		symbolicName = original.symbolicName;
		version = original.version;
		this.status = status;
	}

	public BundleDTOWithStatus(long id, IStatus status, String symbolicName) {
		this.id = id;
		this.status = status;
		this.symbolicName = symbolicName;
	}

	public IStatus status;

}