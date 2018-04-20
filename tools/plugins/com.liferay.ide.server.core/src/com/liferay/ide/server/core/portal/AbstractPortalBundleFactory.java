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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public abstract class AbstractPortalBundleFactory implements PortalBundleFactory {

	@Override
	public IPath canCreateFromPath(IPath location) {
		IPath retval = null;

		if (detectBundleDir(location) && _detectLiferayHome(location.append(".."))) {
			retval = location;
		}
		else if (_detectLiferayHome(location)) {
			File[] directories = FileUtil.getDirectories(location.toFile());

			for (File directory : directories) {
				Path dirPath = new Path(directory.getAbsolutePath());

				if (detectBundleDir(dirPath)) {
					retval = dirPath;

					break;
				}
			}
		}

		return retval;
	}

	@Override
	public IPath canCreateFromPath(Map<String, Object> appServerProperties) {
		IPath retval = null;

		String appServerPath = (String)appServerProperties.get("app.server.dir");
		String appServerParentPath = (String)appServerProperties.get("app.server.parent.dir");
		String appServerDeployPath = (String)appServerProperties.get("app.server.deploy.dir");
		String appServerGlobalLibPath = (String)appServerProperties.get("app.server.lib.global.dir");
		String appServerPortalPath = (String)appServerProperties.get("app.server.portal.dir");

		if (!ServerUtil.verifyPath(appServerPath) || !ServerUtil.verifyPath(appServerParentPath) ||
			!ServerUtil.verifyPath(appServerDeployPath) || !ServerUtil.verifyPath(appServerPortalPath) ||
			!ServerUtil.verifyPath(appServerGlobalLibPath)) {

			return retval;
		}

		IPath appServerLocation = new Path(appServerPath);
		IPath liferayHomelocation = new Path(appServerParentPath);

		if (detectBundleDir(appServerLocation)) {
			retval = appServerLocation;
		}
		else if (_detectLiferayHome(liferayHomelocation)) {
			File[] directories = FileUtil.getDirectories(liferayHomelocation.toFile());

			for (File directory : directories) {
				Path dirPath = new Path(directory.getAbsolutePath());

				if (detectBundleDir(dirPath)) {
					retval = dirPath;

					break;
				}
			}
		}

		return retval;
	}

	@Override
	public String getType() {
		return _bundleFactoryType;
	}

	public void setBundleFactoryType(String type) {
		_bundleFactoryType = type;
	}

	protected abstract boolean detectBundleDir(IPath path);

	private boolean _detectLiferayHome(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		IPath osgiPath = path.append("osgi");

		if (FileUtil.exists(osgiPath)) {
			return true;
		}

		return false;
	}

	private String _bundleFactoryType;

}