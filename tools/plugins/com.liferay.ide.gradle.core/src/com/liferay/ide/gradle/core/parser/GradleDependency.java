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

package com.liferay.ide.gradle.core.parser;

import java.util.Map;

/**
 * @author Lovett Li
 */
public class GradleDependency {

	public GradleDependency(Map<String, String> dep) {
		setGroup(dep.get("group"));
		setName(dep.get("name"));
		setVersion(dep.get("version"));
	}

	public GradleDependency(String group, String name, String version) {
		_group = group;
		_name = name;
		_version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		GradleDependency other = (GradleDependency)obj;

		if (_group == null) {
			if (other._group != null) {
				return false;
			}
		}
		else if (!_group.equals(other._group)) {
			return false;
		}

		if (_name == null) {
			if (other._name != null) {
				return false;
			}
		}
		else if (!_name.equals(other._name)) {
			return false;
		}

		if (_version == null) {
			if (other._version != null) {
				return false;
			}
		}
		else if (!_version.equals(other._version)) {
			return false;
		}

		return true;
	}

	public String getGroup() {
		return _group;
	}

	public String getName() {
		return _name;
	}

	public String getVersion() {
		return _version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_group == null) ? 0 : _group.hashCode());
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + ((_version == null) ? 0 : _version.hashCode());

		return result;
	}

	public void setGroup(String group) {
		_group = group;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setVersion(String version) {
		_version = version;
	}

	@Override
	public String toString() {
		return "Gradle Dependency " + _group + ":" + _name + ":" + _version;
	}

	private String _group;
	private String _name;
	private String _version;

}