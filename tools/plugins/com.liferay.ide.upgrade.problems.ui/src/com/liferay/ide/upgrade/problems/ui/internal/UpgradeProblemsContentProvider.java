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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.navigator.AbstractNavigatorContentProvider;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class UpgradeProblemsContentProvider extends AbstractNavigatorContentProvider {

	public UpgradeProblemsContentProvider() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeProblemsContentProvider.class);

		_upgradePlannerServiceTracker = new ServiceTracker<>(bundle.getBundleContext(), UpgradePlanner.class, null);

		_upgradePlannerServiceTracker.open();
	}

	@Override
	public void dispose() {
		super.dispose();

		_upgradePlannerServiceTracker.close();
	}

	public Object[] getChildren(Object element) {
		if (element instanceof UpgradeProblemsContainer) {
			UpgradeProblemsContainer upgradeProblemsContainer = (UpgradeProblemsContainer)element;

			List<ProjectProblemsContainer> projectProblemsContainers =
				upgradeProblemsContainer.getProjectProblemsConatiners();

			Stream<ProjectProblemsContainer> stream = projectProblemsContainers.stream();

			return stream.filter(
				projectProblemsContainer -> !projectProblemsContainer.isEmpty()
			).toArray();
		}
		else if (element instanceof LegacyProblemsContainer) {
			LegacyProblemsContainer legacyFileProblemsContainer = (LegacyProblemsContainer)element;

			List<ProjectProblemsContainer> projectProblemsContainers =
				legacyFileProblemsContainer.getProjectProblemsConatiners();

			Stream<ProjectProblemsContainer> stream = projectProblemsContainers.stream();

			return stream.filter(
				projectProblemsContainer -> !projectProblemsContainer.isEmpty()
			).toArray();
		}
		else if (element instanceof ProjectProblemsContainer) {
			ProjectProblemsContainer projectProblemsContainer = (ProjectProblemsContainer)element;

			List<FileProblemsContainer> fileProblemsContainers = projectProblemsContainer.getFileProblemsContainers();

			return fileProblemsContainers.toArray();
		}
		else if (element instanceof FileProblemsContainer) {
			FileProblemsContainer fileProblemsContainer = (FileProblemsContainer)element;

			List<UpgradeProblem> upgradeProblems = fileProblemsContainer.getUpgradeProblems();

			Stream<UpgradeProblem> stream = upgradeProblems.stream();

			upgradeProblems = stream.filter(
				upgradeProblem -> upgradeProblem.getStatus() != UpgradeProblem.STATUS_IGNORE
			).collect(
				Collectors.toList()
			);

			return upgradeProblems.toArray();
		}

		return null;
	}

	public Object[] getElements(Object inputElement) {
		UpgradePlanner upgradePlanner = _upgradePlannerServiceTracker.getService();

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		if (upgradePlan == null) {
			return null;
		}

		Collection<UpgradeProblem> upgradeProblems = upgradePlan.getUpgradeProblems();

		UpgradeProblemsContainer upgradeProblemsContainer = new UpgradeProblemsContainer();

		LegacyProblemsContainer legacyProblemsContainer = new LegacyProblemsContainer();

		for (IProject project : CoreUtil.getAllProjects()) {
			try {
				if (!project.hasNature("org.eclipse.jdt.core.javanature") ||
					(LiferayCore.create(IWorkspaceProject.class, project) != null)) {

					continue;
				}
			}
			catch (CoreException ce) {
				continue;
			}

			ProjectProblemsContainer projectProblemsContainer = new ProjectProblemsContainer();

			ProjectProblemsContainer legacyProjectProblemsContainer = new ProjectProblemsContainer();

			projectProblemsContainer.setProjectName(project.getName());

			legacyProjectProblemsContainer.setProjectName(project.getName());

			for (UpgradeProblem upgradeProblem : upgradeProblems) {
				File file = upgradeProblem.getResource();

				if (FileUtil.notExists(file)) {
					continue;
				}

				Path filePath = new Path(file.getPath());

				IPath projectLocation = project.getLocation();

				if (!projectLocation.isPrefixOf(filePath)) {
					continue;
				}

				FileProblemsContainer legacyFileProblemsContainer = projectProblemsContainer.getFileProblemsContainer(
					file);

				if ((upgradeProblem.getType() != null) && Objects.equals(upgradeProblem.getType(), "legacy")) {
					if (legacyFileProblemsContainer == null) {
						legacyFileProblemsContainer = new FileProblemsContainer();

						legacyFileProblemsContainer.setFile(file);

						legacyProjectProblemsContainer.addFileProblemsContainer(legacyFileProblemsContainer);
					}

					legacyFileProblemsContainer.addUpgradeProblem(upgradeProblem);

					continue;
				}

				FileProblemsContainer fileProblemsContainer = projectProblemsContainer.getFileProblemsContainer(file);

				if (fileProblemsContainer == null) {
					fileProblemsContainer = new FileProblemsContainer();

					fileProblemsContainer.setFile(file);

					projectProblemsContainer.addFileProblemsContainer(fileProblemsContainer);
				}

				fileProblemsContainer.addUpgradeProblem(upgradeProblem);
			}

			legacyProblemsContainer.addProjectProblemsContainer(legacyProjectProblemsContainer);

			upgradeProblemsContainer.addProjectProblemsContainer(projectProblemsContainer);
		}

		List<Object> retVal = new ArrayList<>();

		if (upgradeProblemsContainer.isNotEmpty()) {
			retVal.add(upgradeProblemsContainer);
		}

		if (legacyProblemsContainer.isNotEmpty()) {
			retVal.add(legacyProblemsContainer);
		}

		if (retVal.isEmpty()) {
			return null;
		}

		return retVal.toArray();
	}

	public boolean hasChildren(Object element) {
		if (element instanceof UpgradeProblemsContainer) {
			return true;
		}
		else if (element instanceof LegacyProblemsContainer) {
			return true;
		}
		else if (element instanceof ProjectProblemsContainer) {
			return true;
		}
		else if (element instanceof FileProblemsContainer) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element);
	}

	private ServiceTracker<UpgradePlanner, UpgradePlanner> _upgradePlannerServiceTracker;

}