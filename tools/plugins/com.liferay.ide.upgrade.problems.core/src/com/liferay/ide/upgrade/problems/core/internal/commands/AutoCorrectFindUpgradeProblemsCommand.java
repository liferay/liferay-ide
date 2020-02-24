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
import com.liferay.ide.upgrade.plan.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.UpgradeProblemSupport;
import com.liferay.ide.upgrade.problems.core.commands.AutoCorrectFindUpgradeProblemsCommandKeys;
import com.liferay.ide.upgrade.problems.core.internal.UpgradeProblemsCorePlugin;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
@Component(
	property = "id=" + AutoCorrectFindUpgradeProblemsCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class AutoCorrectFindUpgradeProblemsCommand implements UpgradeCommand, UpgradePreview, UpgradeProblemSupport {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<UpgradeProblem> autoCorrectableUpgradeProblems = _findAutoCorrectableUpgradeProblems(progressMonitor);

		if (ListUtil.isEmpty(autoCorrectableUpgradeProblems)) {
			return Status.OK_STATUS;
		}

		autoCorrectableUpgradeProblems.stream(
		).forEach(
			this::_autoCorrectProblem
		);

		refreshProjects(autoCorrectableUpgradeProblems, progressMonitor);

		_upgradePlanner.dispatch(
			new UpgradeCommandPerformedEvent(this, new ArrayList<>(autoCorrectableUpgradeProblems)));

		return Status.OK_STATUS;
	}

	@Override
	public void preview(IProgressMonitor progressMonitor) {
		List<UpgradeProblem> autoCorrectableUpgradeProblems = _findAutoCorrectableUpgradeProblems(progressMonitor);

		if (ListUtil.isEmpty(autoCorrectableUpgradeProblems)) {
			return;
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		upgradeProblems.addAll(autoCorrectableUpgradeProblems);

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, new ArrayList<>(upgradeProblems)));
	}

	private void _autoCorrectProblem(UpgradeProblem upgradeProblem) {
		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectFindUpgradeProblemsCommand.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

		String autoCorrectKey = autoCorrectContext;

		int filterKeyIndex = autoCorrectContext.indexOf(":");

		if (filterKeyIndex > -1) {
			autoCorrectKey = autoCorrectContext.substring(0, filterKeyIndex);
		}

		try {
			String filter = "(&(auto.correct=" + autoCorrectKey + ")(version=" + upgradeProblem.getVersion() + "))";

			Collection<ServiceReference<AutoFileMigrator>> serviceReferences = bundleContext.getServiceReferences(
				AutoFileMigrator.class, filter);

			File file = upgradeProblem.getResource();

			serviceReferences.stream(
			).map(
				bundleContext::getService
			).forEach(
				autoFileMigrator -> {
					try {
						autoFileMigrator.correctProblems(file, Arrays.asList(upgradeProblem));
					}
					catch (AutoFileMigrateException afme) {
						UpgradeProblemsCorePlugin.logError(
							"Error encountered auto migrating file " + file.getAbsolutePath(), afme);
					}
				}
			);
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	private List<UpgradeProblem> _findAutoCorrectableUpgradeProblems(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		if (ListUtil.isNotEmpty(upgradeProblems)) {
			boolean result = _messagePrompt.promptQuestion(
				"Remove the found results?",
				"The found results will be removed if you do this, do you want to continue?");

			if (!result) {
				return Collections.emptyList();
			}

			removeMarkers(upgradeProblems);

			upgradeProblems.clear();
		}

		List<IProject> projects = _resourceSelection.selectProjects(
			"Select projects to search for upgrade problems.", true, ResourceSelection.JAVA_PROJECTS);

		List<String> upgradeVersions = upgradePlan.getUpgradeVersions();

		if (projects.isEmpty()) {
			return Collections.emptyList();
		}

		List<UpgradeProblem> autoCorrectProblems = projects.stream(
		).map(
			FileUtil::getFile
		).map(
			projectFile -> _fileMigration.findUpgradeProblems(
				projectFile, upgradeVersions, Collections.singleton("auto.correct"), progressMonitor)
		).flatMap(
			findProblems -> findProblems.stream()
		).filter(
			findProblem -> ListUtil.notContains((Set<UpgradeProblem>)upgradePlan.getIgnoredProblems(), findProblem)
		).collect(
			Collectors.toList()
		);

		autoCorrectProblems.sort(
			(problem1, problem2) -> {
				Version version1 = new Version(problem1.getVersion());
				Version version2 = new Version(problem2.getVersion());

				return version1.compareTo(version2);
			});

		return autoCorrectProblems;
	}

	@Reference
	private FileMigration _fileMigration;

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}