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

import com.liferay.ide.upgrade.plan.core.FileUpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradeTask;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskCategory;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @author Gregory Amerson
 */
public class StandardUpgradePlan implements UpgradePlan {

	public StandardUpgradePlan(String name, String currentVersion, String targetVersion, Path currentProjectLocation) {
		_name = name;
		_currentVersion = currentVersion;
		_targetVersion = targetVersion;
		_currentProjectLocation = currentProjectLocation;
		_fileUpgradeProblems = new HashSet<>();
	}

	@Override
	public void addFileUpgradeProblems(Collection<FileUpgradeProblem> fileUpgradeProblems) {
		_fileUpgradeProblems.addAll(fileUpgradeProblems);
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
	public Set<FileUpgradeProblem> getFileUpgradeProblems() {
		return _fileUpgradeProblems;
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
	public List<UpgradeTask> getTasks() {
		if (_upgradeTasks == null) {
			Bundle bundle = FrameworkUtil.getBundle(StandardUpgradePlan.class);

			BundleContext bundleContext = bundle.getBundleContext();

			List<UpgradeTask> upgradeTasks = null;

			try {
				List<UpgradeTaskCategory> orderedUpgradeTaskCategories = ServicesLookup.getOrderedServices(
					bundleContext, bundleContext.getServiceReferences(UpgradeTaskCategory.class, null));

				Stream<UpgradeTaskCategory> stream = orderedUpgradeTaskCategories.stream();

				upgradeTasks = stream.flatMap(
					upgradeTaskCategory -> {
						try {
							List<UpgradeTask> orderedUpgradeTasks = ServicesLookup.getOrderedServices(
								bundleContext,
								bundleContext.getServiceReferences(
									UpgradeTask.class, "(categoryId=" + upgradeTaskCategory.getId() + ")"));

							return orderedUpgradeTasks.stream();
						}
						catch (InvalidSyntaxException ise) {
							return null;
						}
					}
				).filter(
					upgradeTask -> upgradeTask.appliesTo(this)
				).collect(
					Collectors.toList()
				);
			}
			catch (InvalidSyntaxException ise) {
				upgradeTasks = Collections.emptyList();
			}

			_upgradeTasks = upgradeTasks;
		}

		return Collections.unmodifiableList(_upgradeTasks);
	}

	@Override
	public void setTargetProjectLocation(Path path) {
		_targetProjectLocation = path;
	}

	private final Path _currentProjectLocation;
	private final String _currentVersion;
	private Set<FileUpgradeProblem> _fileUpgradeProblems;
	private final String _name;
	private Path _targetProjectLocation;
	private final String _targetVersion;
	private List<UpgradeTask> _upgradeTasks;

}