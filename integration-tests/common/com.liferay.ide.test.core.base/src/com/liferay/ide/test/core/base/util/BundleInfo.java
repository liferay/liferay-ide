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

package com.liferay.ide.test.core.base.util;

import com.liferay.ide.core.util.StringPool;

/**
 * @author Terry Jia
 */
public class BundleInfo {

	public String getBundleDir() {
		return _bundleDir;
	}

	public String getBundleZip() {
		return _bundleZip;
	}

	public String getServerDir() {
		return _serverDir;
	}

	public String getType() {
		return _type;
	}

	public String getVersion() {
		return _version;
	}

	public void setBundleDir(String bundleDir) {
		_bundleDir = bundleDir;
	}

	public void setBundleZip(String bundleZip) {
		_bundleZip = bundleZip;
	}

	public void setServerDir(String serverDir) {
		_serverDir = serverDir;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setVersion(String version) {
		_version = version;
	}

	private String _bundleDir = StringPool.BLANK;
	private String _bundleZip = StringPool.BLANK;
	private String _serverDir = StringPool.BLANK;
	private String _type = StringPool.BLANK;
	private String _version = StringPool.BLANK;

}