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

import java.io.IOException;

import java.nio.file.Path;

import java.util.List;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public interface UpgradePlanner {

	public void addListener(UpgradeListener upgradeListener);

	public void completeStep(UpgradeStep upgradeStep);

	public void dispatch(UpgradeEvent upgradeEvent);

	public void dispose(UpgradePlan upgradePlan);

	public UpgradePlan getCurrentUpgradePlan();

	public List<UpgradePlan> loadAllUpgradePlans();

	public UpgradePlan loadUpgradePlan(String name);

	public UpgradePlan newUpgradePlan(
			String name, String currentVersion, String targetVersion, Path sourceCodeLocation,
			String upgradePlanOutline)
		throws IOException;

	public void removeListener(UpgradeListener upgradeListener);

	public void removeUpgradePlan(UpgradePlan upgradePlan);

	public void restartStep(UpgradeStep upgradeStep);

	public void saveUpgradePlan(UpgradePlan upgradePlan);

	public void skipStep(UpgradeStep upgradeStep);

	public void startUpgradePlan(UpgradePlan upgradePlan);

}