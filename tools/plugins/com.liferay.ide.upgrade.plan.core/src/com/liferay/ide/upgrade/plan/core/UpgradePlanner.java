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

import java.nio.file.Path;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface UpgradePlanner {

	public void addListener(UpgradeListener upgradeListener);

	public void dispatch(UpgradeEvent upgradeEvent);

	public UpgradePlan getCurrentUpgradePlan();

	public UpgradePlan loadUpgradePlan(String name);

	public UpgradePlan newUpgradePlan(
		String name, String currentVersion, String targetVersion, Path sourceCodeLocation);

	public void removeListener(UpgradeListener upgradeListener);

	public void restartStep(UpgradeStep upgradeStep);

	public void saveUpgradePlan(UpgradePlan upgradePlan);

	public void skipStep(UpgradeStep upgradeTaskStep);

	public void startUpgradePlan(UpgradePlan upgradePlan);

}