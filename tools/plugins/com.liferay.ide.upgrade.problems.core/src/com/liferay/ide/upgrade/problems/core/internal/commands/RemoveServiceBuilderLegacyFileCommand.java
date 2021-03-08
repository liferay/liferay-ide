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

import com.google.common.base.Objects;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.upgrade.plan.core.MessagePrompt;
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradePreview;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeProblemSupport;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.AutoFileMigratorException;
import com.liferay.ide.upgrade.problems.core.LegacyFileMigration;
import com.liferay.ide.upgrade.problems.core.commands.RemoveServiceBuilderLegacyFileCommandKeys;
import com.liferay.ide.upgrade.problems.core.internal.UpgradeProblemsCorePlugin;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.osgi.framework.VersionRange;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 */
@Component(
	property = "id=" + RemoveServiceBuilderLegacyFileCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = {UpgradeCommand.class, UpgradePreview.class}
)
public class RemoveServiceBuilderLegacyFileCommand implements UpgradeCommand, UpgradePreview, UpgradeProblemSupport {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<UpgradeProblem> removeServiceBuilderLegacyFilesProblems = _findServiceBuilderLegacyFilesProblems(
			progressMonitor);

		if (ListUtil.isEmpty(removeServiceBuilderLegacyFilesProblems)) {
			_upgradePlanner.dispatch(
				new UpgradeCommandPerformedEvent(this, new ArrayList<>(removeServiceBuilderLegacyFilesProblems)));

			return Status.OK_STATUS;
		}

		Stream<UpgradeProblem> removeServiceBuilderLegacyFilesProblemsStream =
			removeServiceBuilderLegacyFilesProblems.stream();

		removeServiceBuilderLegacyFilesProblemsStream.forEach(this::_autoCorrectProblem);

		refreshProjects(removeServiceBuilderLegacyFilesProblems, progressMonitor);

		_upgradePlanner.dispatch(
			new UpgradeCommandPerformedEvent(this, new ArrayList<>(removeServiceBuilderLegacyFilesProblems)));

		return Status.OK_STATUS;
	}

	@Override
	public void preview(IProgressMonitor progressMonitor) {
		List<UpgradeProblem> removeServiceBuilderLegacyFilesProblems = _findServiceBuilderLegacyFilesProblems(
			progressMonitor);

		if (ListUtil.isEmpty(removeServiceBuilderLegacyFilesProblems)) {
			return;
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Stream<UpgradeProblem> upgradeProblemsStream = upgradeProblems.stream();

		List<UpgradeProblem> removeLeacyFileProblemList = upgradeProblemsStream.filter(
			problem -> Objects.equal(problem.getType(), "legacy")
		).collect(
			Collectors.toList()
		);

		upgradeProblems.addAll(removeServiceBuilderLegacyFilesProblems);

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, new ArrayList<>(removeLeacyFileProblemList)));
	}

	private void _autoCorrectProblem(UpgradeProblem legacyUpgradeProblem) {
		Bundle bundle = FrameworkUtil.getBundle(RemoveServiceBuilderLegacyFileCommand.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String autoCorrectContext = legacyUpgradeProblem.getAutoCorrectContext();

		String autoCorrectKey = autoCorrectContext;

		int filterKeyIndex = autoCorrectContext.indexOf(":");

		if (filterKeyIndex > -1) {
			autoCorrectKey = autoCorrectContext.substring(0, filterKeyIndex);
		}

		try {
			Collection<ServiceReference<AutoFileMigrator>> serviceReferences = bundleContext.getServiceReferences(
				AutoFileMigrator.class, "(auto.correct=" + autoCorrectKey + ")");

			File file = legacyUpgradeProblem.getResource();

			Stream<ServiceReference<AutoFileMigrator>> serviceReferencesStream = serviceReferences.stream();

			serviceReferencesStream.filter(
				ref -> {
					Dictionary<String, Object> serviceProperties = ref.getProperties();

					return Optional.ofNullable(
						serviceProperties.get("version")
					).map(
						Object::toString
					).map(
						VersionRange::valueOf
					).filter(
						range -> range.includes(new Version(legacyUpgradeProblem.getVersion()))
					).isPresent();
				}
			).filter(
				ref -> {
					Dictionary<String, Object> serviceProperties = ref.getProperties();

					return Optional.ofNullable(
						serviceProperties.get("problem.type")
					).filter(
						type -> Objects.equal(type, "legacy")
					).isPresent();
				}
			).map(
				bundleContext::getService
			).forEach(
				autoFileMigrator -> {
					try {
						autoFileMigrator.correctProblems(file, Arrays.asList(legacyUpgradeProblem));
					}
					catch (AutoFileMigratorException afme) {
						UpgradeProblemsCorePlugin.logError(
							"Error encountered auto migrating file " + file.getAbsolutePath(), afme);
					}
				}
			);
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	private List<UpgradeProblem> _findServiceBuilderLegacyFilesProblems(IProgressMonitor progressMonitor) {
		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Stream<UpgradeProblem> removeLegacyFileProblems = upgradeProblems.stream();

		List<UpgradeProblem> removeLeacyFileProblemList = removeLegacyFileProblems.filter(
			problem -> Objects.equal(problem.getType(), "legacy")
		).collect(
			Collectors.toList()
		);

		if (ListUtil.isNotEmpty(removeLeacyFileProblemList)) {
			boolean result = _messagePrompt.promptQuestion(
				"Remove the found results?",
				"The found results will be removed if you do this, do you want to continue?");

			if (!result) {
				return Collections.emptyList();
			}

			removeMarkers(removeLeacyFileProblemList);

			removeLeacyFileProblemList.clear();
		}

		List<IProject> projects = _resourceSelection.selectProjects(
			"Select projects to search for finding legacy file to remove.", true, ResourceSelection.JAVA_PROJECTS);

		if (projects.isEmpty()) {
			return Collections.emptyList();
		}

		Stream<IProject> removeServiceBulderLegacyFileStream = projects.stream();

		List<String> upgradeVersions = upgradePlan.getUpgradeVersions();

		List<UpgradeProblem> legacyAutoCorrectProblems = removeServiceBulderLegacyFileStream.filter(
			ProjectUtil::isServiceBuilderProject
		).map(
			FileUtil::getFile
		).map(
			projectFile -> _legacyFileMigration.findUpgradeProblems(
				projectFile, upgradeVersions, Collections.singleton("auto.correct"), progressMonitor)
		).flatMap(
			findProblems -> findProblems.stream()
		).filter(
			findProblem -> ListUtil.notContains((Set<UpgradeProblem>)upgradePlan.getIgnoredProblems(), findProblem)
		).collect(
			Collectors.toList()
		);

		legacyAutoCorrectProblems.sort(
			(problem1, problem2) -> {
				Version version1 = new Version(problem1.getVersion());
				Version version2 = new Version(problem2.getVersion());

				return version1.compareTo(version2);
			});

		return legacyAutoCorrectProblems;
	}

	@Reference
	private LegacyFileMigration _legacyFileMigration;

	@Reference
	private MessagePrompt _messagePrompt;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}