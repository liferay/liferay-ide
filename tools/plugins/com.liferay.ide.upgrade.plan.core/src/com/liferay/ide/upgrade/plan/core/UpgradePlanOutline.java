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

import org.eclipse.core.runtime.Adapters;

/**
 * @author Simon Jiang
 */
public class UpgradePlanOutline implements Comparable<IUpgradePlanOutline>, IUpgradePlanOutline {

	public UpgradePlanOutline(String displayName, String location, boolean offline) {
		_displayName = displayName;
		_location = location;
		_offline = offline;
	}

	public int compareTo(IUpgradePlanOutline outline) {
		if (outline == null) {
			return 0;
		}

		return _displayName.compareTo(outline.getName());
	}

	public boolean equals(Object object) {
		if ((object instanceof UpgradePlanOutline) == false) {
			return false;
		}

		UpgradePlanOutline baseUpgradeOutline = Adapters.adapt(object, UpgradePlanOutline.class);

		if (baseUpgradeOutline == null) {
			return false;
		}

		if (isEqualIgnoreCase(_displayName, baseUpgradeOutline._displayName) &&
			isEqualIgnoreCase(_location, baseUpgradeOutline._location) && (_offline == baseUpgradeOutline._offline)) {

			return true;
		}

		return false;
	}

	@Override
	public String getLocation() {
		return _location;
	}

	@Override
	public String getName() {
		return _displayName;
	}

	public boolean isEqualIgnoreCase(String original, String target) {
		if (original != null) {
			return original.equalsIgnoreCase(target);
		}

		if (target == null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isOffline() {
		return _offline;
	}

	@Override
	public String toString() {
		return _displayName + ", " + _location + ", " + Boolean.toString(_offline);
	}

	private String _displayName;
	private String _location;
	private boolean _offline;

}