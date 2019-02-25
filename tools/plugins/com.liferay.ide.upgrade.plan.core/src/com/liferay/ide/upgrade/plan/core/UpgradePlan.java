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

import java.util.Collection;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public interface UpgradePlan {

	public void addUpgradeProblems(Collection<UpgradeProblem> upgradeProblems);

	public Path getCurrentProjectLocation();

	public String getCurrentVersion();

	public String getName();

	public Path getTargetProjectLocation();

	public String getTargetVersion();

	public List<UpgradeTask> getTasks();

	public List<String> getUpgradeCategories();

	public Collection<UpgradeProblem> getUpgradeProblems();

	public List<String> getUpgradeVersions();

	public void setTargetProjectLocation(Path path);

}