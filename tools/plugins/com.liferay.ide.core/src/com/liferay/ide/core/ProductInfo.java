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

package com.liferay.ide.core;

import com.google.gson.annotations.SerializedName;

/**
 * @author Simon Jiang
 */
public class ProductInfo {

	public String getAppServerTomcatVersion() {
		return _appServerTomcatVersion;
	}

	public String getBundleUrl() {
		return _bundleUrl;
	}

	public String getLiferayDockerImage() {
		return _liferayDockerImage;
	}

	public String getLiferayProductVersion() {
		return _liferayProductVersion;
	}

	public String getReleaseDate() {
		return _releaseDate;
	}

	public String getTargetPlatformVersion() {
		return _targetPlatformVersion;
	}

	public boolean isInitialVersion() {
		return _initialVersion;
	}

	@SerializedName("appServerTomcatVersion")
	private String _appServerTomcatVersion;

	@SerializedName("bundleUrl")
	private String _bundleUrl;

	@SerializedName("initialVersion")
	private boolean _initialVersion;

	@SerializedName("liferayDockerImage")
	private String _liferayDockerImage;

	@SerializedName("liferayProductVersion")
	private String _liferayProductVersion;

	@SerializedName("releaseDate")
	private String _releaseDate;

	@SerializedName("targetPlatformVersion")
	private String _targetPlatformVersion;

}