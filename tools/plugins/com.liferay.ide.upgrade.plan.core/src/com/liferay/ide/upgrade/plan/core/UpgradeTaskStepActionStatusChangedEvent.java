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
public class UpgradeTaskStepActionStatusChangedEvent implements UpgradeEvent {

	public UpgradeTaskStepActionStatusChangedEvent(
		UpgradeTaskStepAction upgradeTaskStepAction, UpgradeElementStatus oldStatus, UpgradeElementStatus newStatus) {

		_upgradeTaskStepAction = upgradeTaskStepAction;
		_oldStatus = oldStatus;
		_newStatus = newStatus;
	}

	public UpgradeElementStatus getNewStatus() {
		return _newStatus;
	}

	public UpgradeElementStatus getOldStatus() {
		return _oldStatus;
	}

	public UpgradeTaskStepAction getUpgradeTaskStepAction() {
		return _upgradeTaskStepAction;
	}

	private final UpgradeElementStatus _newStatus;
	private final UpgradeElementStatus _oldStatus;
	private final UpgradeTaskStepAction _upgradeTaskStepAction;

}