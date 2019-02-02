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

package com.liferay.ide.upgrade.plan.core;

import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 */
public class BaseUpgradeTaskCategory implements UpgradeTaskCategory {

	@Activate
	public void activate(ComponentContext componentContext) {
		Dictionary<String, Object> properties = componentContext.getProperties();

		_id = _getProperty(properties, "id");
		_imagePath = _getProperty(properties, "imagePath");
		_title = _getProperty(properties, "title");
	}

	@Override
	public String getDescription() {
		return getTitle();
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getImagePath() {
		return _imagePath;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	private String _getProperty(Dictionary<String, Object> properties, String key) {
		Object value = properties.get(key);

		if (value instanceof String) {
			return (String)value;
		}

		return null;
	}

	private String _id;
	private String _imagePath;
	private String _title;

}