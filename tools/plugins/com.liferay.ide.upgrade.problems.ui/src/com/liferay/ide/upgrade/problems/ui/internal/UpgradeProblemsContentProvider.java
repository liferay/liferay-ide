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
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 */
public class UpgradeProblemsContentProvider extends AbstractNavigatorContentProvider {

	public UpgradeProblemsContentProvider() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradeProblemsContentProvider.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_upgradePlannerServiceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

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
		else if (element instanceof ProjectProblemsContainer) {
			ProjectProblemsContainer projectProblemsContainer = (ProjectProblemsContainer)element;

			List<FileProblemsContainer> fileProblemsContainers = projectProblemsContainer.getFileProblemsContainers();

			return fileProblemsContainers.toArray();
		}
		else if (element instanceof FileProblemsContainer) {
			FileProblemsContainer fileProblemsContainer = (FileProblemsContainer)element;

			List<UpgradeProblem> upgradeProblems = fileProblemsContainer.getUpgradeProblems();

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

			projectProblemsContainer.setProjectName(project.getName());

			for (UpgradeProblem upgradeProblem : upgradeProblems) {
				IResource resource = upgradeProblem.getResource();

				File file = new File(resource.getLocationURI());

				Path filePath = new Path(file.getPath());

				IPath projectLocation = project.getLocation();

				if (!projectLocation.isPrefixOf(filePath)) {
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

			upgradeProblemsContainer.addProjectProblemsContainer(projectProblemsContainer);
		}

		if (upgradeProblemsContainer.isNotEmpty()) {
			return new Object[] {upgradeProblemsContainer};
		}
		else {
			return null;
		}
	}

	public boolean hasChildren(Object element) {
		if (element instanceof UpgradeProblemsContainer) {
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