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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class StandardUpgradePlan implements UpgradePlan {

	public StandardUpgradePlan(String name, String currentVersion, String targetVersion, Path currentProjectLocation) {
		_name = name;
		_currentVersion = currentVersion;
		_targetVersion = targetVersion;
		_currentProjectLocation = currentProjectLocation;
		_upgradeProblems = new HashSet<>();
	}

	@Override
	public void addUpgradeProblems(Collection<UpgradeProblem> upgradeProblems) {
		_upgradeProblems.addAll(upgradeProblems);
	}

	@Override
	public Path getCurrentProjectLocation() {
		return _currentProjectLocation;
	}

	@Override
	public String getCurrentVersion() {
		return _currentVersion;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Path getTargetProjectLocation() {
		return _targetProjectLocation;
	}

	@Override
	public String getTargetVersion() {
		return _targetVersion;
	}

	@Override
	public Set<UpgradeProblem> getUpgradeProblems() {
		return _upgradeProblems;
	}

	public UpgradeStep getUpgradeStep(String title) {
		for (UpgradeStep upgradeStep : _upgradeSteps) {
			UpgradeStep step = _getUpgradeStep(upgradeStep, title);

			if (step != null) {
				return step;
			}
		}

		return null;
	}

	@Override
	public List<UpgradeStep> getUpgradeSteps() {
		return Collections.unmodifiableList(_upgradeSteps);
	}

	@Override
	public List<String> getUpgradeVersions() {
		String currentVersion = getCurrentVersion();
		String targetVersion = getTargetVersion();

		boolean begin = false;

		List<String> upgradeVersions = new ArrayList<>();

		for (String liferayVersion : _liferayVersions) {
			if (begin) {
				upgradeVersions.add(liferayVersion);

				if (liferayVersion.equals(targetVersion)) {
					break;
				}
			}
			else if (liferayVersion.equals(currentVersion)) {
				begin = true;
			}
		}

		return upgradeVersions;
	}

	@Override
	public void setTargetProjectLocation(Path path) {
		_targetProjectLocation = path;
	}

	public void setUpgradeSteps(List<UpgradeStep> upgradeSteps) {
		_upgradeSteps = upgradeSteps;
	}

	private UpgradeStep _getUpgradeStep(UpgradeStep upgradeStep, String title) {
		if (StringUtil.equals(upgradeStep.getTitle(), title)) {
			return upgradeStep;
		}

		List<UpgradeStep> children = upgradeStep.getChildren();

		for (UpgradeStep child : children) {
			_getUpgradeStep(child, title);
		}

		return null;
	}

	@SuppressWarnings("serial")
	private static final List<String> _liferayVersions = new ArrayList<String>() {
		{
			add("6.2");
			add("7.0");
			add("7.1");
		}
	};

	private final Path _currentProjectLocation;
	private final String _currentVersion;
	private final String _name;
	private Path _targetProjectLocation;
	private final String _targetVersion;
	private Set<UpgradeProblem> _upgradeProblems;
	private List<UpgradeStep> _upgradeSteps;

}