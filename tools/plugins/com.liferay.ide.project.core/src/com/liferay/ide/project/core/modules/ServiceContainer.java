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

package com.liferay.ide.project.core.modules;

import java.util.List;

/**
 * @author Lovett Li
 */
public class ServiceContainer {

	public ServiceContainer(List<String> serviceList) {
		_serviceList = serviceList;
	}

	public ServiceContainer(String bundleGroup, String bundleName, String bundleVersion) {
		_bundleGroup = bundleGroup;
		_bundleName = bundleName;
		_bundleVersion = bundleVersion;
	}

	public String getBundleGroup() {
		return _bundleGroup;
	}

	public String getBundleName() {
		return _bundleName;
	}

	public String getBundleVersion() {
		return _bundleVersion;
	}

	public List<String> getServiceList() {
		return _serviceList;
	}

	public void setBundleGroup(String bundleGroup) {
		_bundleGroup = bundleGroup;
	}

	public void setBundleName(String bundleName) {
		_bundleName = bundleName;
	}

	public void setBundleVersion(String bundleVersion) {
		_bundleVersion = bundleVersion;
	}

	public void setServiceList(List<String> serviceList) {
		_serviceList = serviceList;
	}

	private String _bundleGroup;
	private String _bundleName;
	private String _bundleVersion;
	private List<String> _serviceList;

}