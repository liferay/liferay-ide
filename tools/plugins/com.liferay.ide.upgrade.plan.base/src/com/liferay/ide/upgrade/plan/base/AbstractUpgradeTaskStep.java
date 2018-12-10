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

package com.liferay.ide.upgrade.plan.base;

import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;

/**
 * @author Terry Jia
 */
public abstract class AbstractUpgradeTaskStep implements UpgradeTaskStep {

	@Activate
	public void activate(Map<String, Object> properties) {
		_properties = properties;
	}

	public Object getProperty(String key) {
		return _properties.get(key);
	}

	private Map<String, Object> _properties;

}