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

/**
 * @author Gregory Amerson
 */
public interface UpgradePlanElement {

	public default boolean appliesTo(UpgradePlan upgradePlan) {
		return true;
	}

	public String getDescription();

	public default double getDoubleProperty(Dictionary<String, Object> properties, String key) {
		Object value = properties.get(key);

		if (value != null) {
			try {
				return Double.parseDouble(value.toString());
			}
			catch (NumberFormatException nfe) {
			}
		}

		return -1;
	}

	public String getId();

	public String getImagePath();

	public UpgradePlanElementStatus getStatus();

	public default String getStringProperty(Dictionary<String, Object> properties, String key) {
		Object value = properties.get(key);

		if (value instanceof String) {
			return (String)value;
		}

		return null;
	}

	public String getTitle();

	public void setStatus(UpgradePlanElementStatus upgradePlanElementStatus);

}