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

package com.liferay.ide.server.core.portal.docker;

import com.google.gson.annotations.SerializedName;

import org.eclipse.core.runtime.Adapters;

/**
 * @author Simon Jiang
 */
public class DockerRegistryConfiguration {

	public DockerRegistryConfiguration() {
	}

	public DockerRegistryConfiguration(boolean activity, String name, String regitstryUrl) {
		_activity = activity;
		_name = name;
		_regitstryUrl = regitstryUrl;
	}

	public DockerRegistryConfiguration(DockerRegistryConfiguration configuration) {
		_activity = configuration.isActivity();
		_name = configuration.getName();
		_regitstryUrl = configuration.getRegitstryUrl();
	}

	@Override
	public boolean equals(Object object) {
		if ((object instanceof DockerRegistryConfiguration) == false) {
			return false;
		}

		DockerRegistryConfiguration targetDockerRegistryConfiguration = Adapters.adapt(
			object, DockerRegistryConfiguration.class);

		if (targetDockerRegistryConfiguration == null) {
			return false;
		}

		if (_isEqualIgnoreCase(_name, targetDockerRegistryConfiguration._name) &&
			_isEqualIgnoreCase(_regitstryUrl, targetDockerRegistryConfiguration._regitstryUrl) &&
			(_activity == targetDockerRegistryConfiguration._activity)) {

			return true;
		}

		return super.equals(object);
	}

	public String getName() {
		return _name;
	}

	public String getRegitstryUrl() {
		return _regitstryUrl;
	}

	public boolean isActivity() {
		return _activity;
	}

	public void setActivity(boolean activity) {
		_activity = activity;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setRegitstryUrl(String regitstryUrl) {
		_regitstryUrl = regitstryUrl;
	}

	private boolean _isEqualIgnoreCase(String original, String target) {
		if (original != null) {
			return original.equalsIgnoreCase(target);
		}

		if (target == null) {
			return true;
		}

		return false;
	}

	@SerializedName("activity")
	private boolean _activity;

	@SerializedName("name")
	private String _name;

	@SerializedName("regitstryUrl")
	private String _regitstryUrl;

}