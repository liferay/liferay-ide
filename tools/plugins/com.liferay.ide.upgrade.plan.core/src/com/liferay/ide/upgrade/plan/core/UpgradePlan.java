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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Seiphon Wang
 */
public interface UpgradePlan {

	public void addIgnoredProblem(UpgradeProblem ignoredProblem);

	public void addIgnoredProblems(Collection<UpgradeProblem> ignoredProblems);

	public void addUpgradeProblems(Collection<UpgradeProblem> upgradeProblems);

	public String getCurrentVersion();

	public Collection<UpgradeProblem> getIgnoredProblems();

	public String getName();

	public String getTargetVersion();

	public Map<String, String> getUpgradeContext();

	public IUpgradePlanOutline getUpgradePlanOutline();

	public Collection<UpgradeProblem> getUpgradeProblems();

	public List<UpgradeStep> getUpgradeSteps();

	public List<String> getUpgradeVersions();

	public void removeIgnoredProblems(Collection<UpgradeProblem> ignoredProblems);

}