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

/**
 * @author Gregory Amerson
 */
public class UpgradePlanElementStatusChangedEvent implements UpgradeEvent {

	public UpgradePlanElementStatusChangedEvent(
		UpgradePlanElement upgradePlanElement, UpgradePlanElementStatus oldStatus, UpgradePlanElementStatus newStatus) {

		_upgradePlanElement = upgradePlanElement;
		_oldStatus = oldStatus;
		_newStatus = newStatus;
	}

	public UpgradePlanElementStatus getNewStatus() {
		return _newStatus;
	}

	public UpgradePlanElementStatus getOldStatus() {
		return _oldStatus;
	}

	public UpgradePlanElement getUpgradePlanElement() {
		return _upgradePlanElement;
	}

	private final UpgradePlanElementStatus _newStatus;
	private final UpgradePlanElementStatus _oldStatus;
	private final UpgradePlanElement _upgradePlanElement;

}