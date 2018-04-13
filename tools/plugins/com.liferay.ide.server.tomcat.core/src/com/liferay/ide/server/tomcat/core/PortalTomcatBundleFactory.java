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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.portal.AbstractPortalBundleFactory;
import com.liferay.ide.server.core.portal.PortalBundle;

import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalTomcatBundleFactory extends AbstractPortalBundleFactory {

	@Override
	public PortalBundle create(IPath location) {
		return new PortalTomcatBundle(location);
	}

	@Override
	public PortalBundle create(Map<String, String> appServerProperties) {
		return new PortalTomcatBundle(appServerProperties);
	}

	@Override
	protected boolean detectBundleDir(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		IPath binPath = path.append("bin");
		IPath confPath = path.append("conf");
		IPath libPath = path.append("lib");
		IPath webappPath = path.append("webapps");

		if (FileUtil.exists(binPath) && FileUtil.exists(confPath) && FileUtil.exists(libPath) &&
			FileUtil.exists(webappPath)) {

			return true;
		}

		return false;
	}

}