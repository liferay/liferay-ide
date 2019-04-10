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

import java.util.Collections;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractLiferayProjectProvider
	implements Comparable<ILiferayProjectProvider>, ILiferayProjectProvider {

	public AbstractLiferayProjectProvider(Class<?>[] types) {
		_classTypes = types;
	}

	public int compareTo(ILiferayProjectProvider provider) {
		if (provider == null) {
			return 0;
		}

		return _shortName.compareTo(provider.getShortName());
	}

	public <T> List<T> getData(String key, Class<T> type, Object... params) {
		return Collections.emptyList();
	}

	public String getDisplayName() {
		return _displayName;
	}

	public int getPriority() {
		return _priority;
	}

	public String getProjectType() {
		return _projectType;
	}

	public String getShortName() {
		return _shortName;
	}

	public boolean isDefault() {
		return _isDefault;
	}

	public boolean provides(Class<?> type) {
		if ((type == null) || (_classTypes == null)) {
			return false;
		}

		for (Class<?> classType : _classTypes) {
			if (classType.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}

	public void setDefault(boolean isDefault) {
		_isDefault = isDefault;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public void setPriority(int priority) {
		_priority = priority;
	}

	public void setProjectType(String type) {
		_projectType = type;
	}

	public void setShortName(String shortName) {
		_shortName = shortName;
	}

	private Class<?>[] _classTypes;
	private String _displayName;
	private boolean _isDefault;
	private int _priority;
	private String _projectType;
	private String _shortName;

}