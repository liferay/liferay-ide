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
import com.liferay.ide.upgrade.plan.core.ResourceSelection;
import com.liferay.ide.upgrade.plan.core.UpgradeCommand;
import com.liferay.ide.upgrade.plan.core.UpgradeCommandPerformedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.MarkerSupport;
import com.liferay.ide.upgrade.problems.core.commands.AutoCorrectFindUpgradeProblemsCommandKeys;
import com.liferay.ide.upgrade.problems.core.internal.UpgradeProblemsCorePlugin;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@Component(
	property = "id=" + AutoCorrectFindUpgradeProblemsCommandKeys.ID, scope = ServiceScope.PROTOTYPE,
	service = UpgradeCommand.class
)
public class AutoCorrectFindUpgradeProblemsCommand implements MarkerSupport, UpgradeCommand {

	@Override
	public IStatus perform(IProgressMonitor progressMonitor) {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select projects to search for upgrade problems.", true, ResourceSelection.JAVA_PROJECTS);

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

		List<String> upgradeVersions = upgradePlan.getUpgradeVersions();

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		Stream<UpgradeProblem> ugradeProblemsStream = upgradeProblems.stream();

		ugradeProblemsStream.map(
			this::findMarker
		).filter(
			this::markerExists
		).forEach(
			this::deleteMarker
		);

		upgradeProblems.clear();

		Stream<IProject> stream = projects.stream();

		stream.forEach(
			project -> {
				File searchFile = FileUtil.getFile(project);

				List<UpgradeProblem> foundUpgradeProblems = _fileMigration.findUpgradeProblems(
					searchFile, upgradeVersions, Collections.singleton("auto.correct"), progressMonitor);

				upgradePlan.addUpgradeProblems(foundUpgradeProblems);

				addMarkers(foundUpgradeProblems);
			});

		_autoCorrectProblem(upgradePlan.getUpgradeProblems());

		_upgradePlanner.dispatch(new UpgradeCommandPerformedEvent(this, new ArrayList<>(upgradeProblems)));

		return Status.OK_STATUS;
	}

	private void _autoCorrectProblem(Collection<UpgradeProblem> upgradeProblems) {
		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectFindUpgradeProblemsCommand.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.filter(
			upgradeProblem -> upgradeProblem.getStatus() != UpgradeProblem.STATUS_IGNORE
		).filter(
			upgradeProblem -> FileUtil.exists(upgradeProblem.getResource())
		).filter(
			upgradeProblem -> Objects.nonNull(upgradeProblem.getAutoCorrectContext())
		).forEach(
			upgradeProblem -> {
				String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

				String autoCorrectKey = autoCorrectContext;

				int filterKeyIndex = autoCorrectContext.indexOf(":");

				if (filterKeyIndex > -1) {
					autoCorrectKey = autoCorrectContext.substring(0, filterKeyIndex);
				}

				try {
					String filter =
						"(&(auto.correct=" + autoCorrectKey + ")(version=" + upgradeProblem.getVersion() + "))";

					Collection<ServiceReference<AutoFileMigrator>> serviceReferences =
						bundleContext.getServiceReferences(AutoFileMigrator.class, filter);

					IResource resource = upgradeProblem.getResource();

					File file = FileUtil.getFile(resource);

					int problemsCorrected = 0;

					for (ServiceReference<AutoFileMigrator> reference : serviceReferences) {
						AutoFileMigrator autoMigrator = bundleContext.getService(reference);

						try {
							problemsCorrected =
								problemsCorrected + autoMigrator.correctProblems(file, Arrays.asList(upgradeProblem));
						}
						catch (AutoFileMigrateException afme) {
							UpgradeProblemsCorePlugin.logError(
								"Error encountered auto migrating file " + file.getAbsolutePath(), afme);
						}
					}

					if ((problemsCorrected > 0) && (resource != null)) {
						_resolveMarker(upgradeProblem);
					}
				}
				catch (InvalidSyntaxException ise) {
				}
			}
		);
	}

	private void _resolveMarker(UpgradeProblem upgradeProblem) {
		upgradeProblem.setStatus(UpgradeProblem.STATUS_RESOLVED);

		IMarker marker = findMarker(upgradeProblem);

		if (marker != null) {
			try {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				marker.setAttribute(IMarker.DONE, Boolean.TRUE);
				marker.setAttribute("upgradeProblem.resolved", Boolean.TRUE);
			}
			catch (CoreException ce) {
			}
		}
	}

	@Reference
	private FileMigration _fileMigration;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}