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

import com.liferay.ide.core.util.ListUtil;

import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public interface UpgradeStep {

	public default boolean appliesTo(UpgradePlan upgradePlan) {
		return true;
	}

	public boolean completed();

	public boolean enabled();

	public String getCategoryId();

	public String[] getChildIds();

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

	public double getOrder();

	public String getParentId();

	public UpgradeStepRequirement getRequirement();

	public UpgradeStepStatus getStatus();

	public default String getStringProperty(Dictionary<String, Object> properties, String key) {
		return getStringProperty(properties, key, null);
	}

	public default String getStringProperty(Dictionary<String, Object> properties, String key, String defaultValue) {
		Object value = properties.get(key);

		if (value instanceof String) {
			return (String)value;
		}

		return defaultValue;
	}

	public String getTitle();

	public String getUrl();

	public default <T extends UpgradeStep> boolean isEqual(List<T> source, List<T> target) {
		if (source == null) {
			if (target == null) {
				return true;
			}

			return false;
		}

		if (target == null) {
			return false;
		}

		if (source.size() != target.size()) {
			return false;
		}

		if (ListUtil.isEmpty(source) && ListUtil.isEmpty(target)) {
			return true;
		}

		Stream<T> targetStream = target.stream();

		List<String> targetElementIds = targetStream.map(
			element -> element.getId()
		).collect(
			Collectors.toList()
		);

		Stream<T> sourceStream = source.stream();

		return sourceStream.filter(
			element -> targetElementIds.contains(element.getId())
		).findAny(
		).isPresent();
	}

	public default boolean isEqualIgnoreCase(String original, String target) {
		if (original != null) {
			return original.equalsIgnoreCase(target);
		}

		if (target == null) {
			return true;
		}

		return false;
	}

	public default IStatus perform(IProgressMonitor progressMonitor) {
		return Status.OK_STATUS;
	}

	public void setStatus(UpgradeStepStatus upgradeStepStatus);

}