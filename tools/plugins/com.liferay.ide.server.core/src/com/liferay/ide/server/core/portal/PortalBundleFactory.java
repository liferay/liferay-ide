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

import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * @author Gregory Amerson
 */
public interface PortalBundleFactory {

	public IPath canCreateFromPath(IPath location);

	public IPath canCreateFromPath(Map<String, Object> appServerProperties);

	public PortalBundle create(IPath location);

	public PortalBundle create(Map<String, String> appServerProperties);

	public String getType();

	public String EXTENSION_ID = "com.liferay.ide.server.core.portalBundles";

}