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

package com.liferay.ide.upgrade.problems.core.internal.commands;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.UpgradeProblemSupport;
import com.liferay.ide.upgrade.problems.core.commands.FindUpgradeProblemsCommandKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Seiphon Wang
 */
@Component(
	property = "id=" + FindUpgradeProblemsCommandKeys.ID, scope = ServiceScope.PROTOTYPE, service = UpgradeCommand.class
)
public class FindUpgradeProblemsCommand implements UpgradeCommand, UpgradeProblemSupport {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select projects to search for upgrade problems.", true, ResourceSelection.JAVA_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Collection<UpgradeProblem> ignoredProblems = upgradePlan.getIgnoredProblems();

		if ((ignoredProblems == null) || ignoredProblems.isEmpty()) {
			Set<UpgradeProblem> ignoredProblemSet = upgradeProblems.stream(
			).filter(
				problem -> UpgradeProblem.STATUS_IGNORE == problem.getStatus()
			).collect(
				Collectors.toSet()
			);

			upgradePlan.addIgnoredProblems(ignoredProblemSet);
		}

		upgradeProblems.stream(
		).map(
			this::findMarker
		).filter(
			this::markerExists
		).forEach(
			this::deleteMarker
		);

		upgradeProblems.clear();

		List<String> upgradeVersions = upgradePlan.getUpgradeVersions();

		List<UpgradeProblem> foundUpgradeProblems = projects.stream(
		).map(
			FileUtil::getFile
		).map(
			searchFile -> _fileMigration.findUpgradeProblems(searchFile, upgradeVersions, progressMonitor)
		).flatMap(
			findProblems -> findProblems.stream()
		).filter(
			findProblem -> ListUtil.notContains((Set<UpgradeProblem>)upgradePlan.getIgnoredProblems(), findProblem)
		).collect(
			Collectors.toList()
		);

		refreshProjects(foundUpgradeProblems, progressMonitor);

		upgradePlan.addUpgradeProblems(foundUpgradeProblems);

		addMarkers(upgradePlan.getUpgradeProblems());

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, new ArrayList<>(upgradeProblems)));

		return Status.OK_STATUS;
	}

	@Reference
	private FileMigration _fileMigration;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}