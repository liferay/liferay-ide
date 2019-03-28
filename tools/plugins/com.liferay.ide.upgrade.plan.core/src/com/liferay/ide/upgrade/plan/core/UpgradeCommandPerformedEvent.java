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

import java.util.List;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class UpgradeCommandPerformedEvent implements UpgradeEvent {

	public UpgradeCommandPerformedEvent(UpgradeCommand upgradeCommand, List<?> workItems) {
		_upgradeCommand = upgradeCommand;
		_workItems = workItems;
	}

	public UpgradeCommand getUpgradeCommand() {
		return _upgradeCommand;
	}

	public List<?> getWorkItems() {
		return _workItems;
	}

	private UpgradeCommand _upgradeCommand;
	private List<?> _workItems;

}