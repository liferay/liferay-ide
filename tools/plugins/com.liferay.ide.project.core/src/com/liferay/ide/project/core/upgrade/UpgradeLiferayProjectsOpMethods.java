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

package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.AbstractUpgradeProjectHandler;
import com.liferay.ide.project.core.UpgradeProjectHandlerReader;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Simon Jiang
 */
public class UpgradeLiferayProjectsOpMethods {

	public static final Status execute(UpgradeLiferayProjectsOp op, ProgressMonitor pm) {
		Status retval = Status.createOkStatus();

		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Upgrading Liferay plugin projects (this process may take several minutes)", 30);

		ElementList<NamedItem> projectItems = op.getSelectedProjects();
		ElementList<NamedItem> upgradeActions = op.getSelectedActions();
		String runtimeName = _getter.get(op.getRuntimeName());
		List<String> projectItemNames = new ArrayList<>();
		List<String> projectActionItems = new ArrayList<>();

		for (NamedItem projectItem : projectItems) {
			projectItemNames.add(_getter.get(projectItem.getName()));
		}

		for (NamedItem upgradeAction : upgradeActions) {
			projectActionItems.add(_getter.get(upgradeAction.getName()));
		}

		Status[] upgradeStatuses = _performUpgrade(projectItemNames, projectActionItems, runtimeName, monitor);

		for (Status s : upgradeStatuses) {
			if (!s.ok()) {
				retval = Status.createErrorStatus(
					"Some upgrade actions failed, please see Eclipse error log for more details");
			}
		}

		return retval;
	}

	private static HashMap<String, AbstractUpgradeProjectHandler> _getActionMap(
		List<AbstractUpgradeProjectHandler> upgradeActions) {

		HashMap<String, AbstractUpgradeProjectHandler> actionMaps = new HashMap<>();

		for (AbstractUpgradeProjectHandler upgradeHandler : upgradeActions) {
			actionMaps.put(upgradeHandler.getName(), upgradeHandler);
		}

		return actionMaps;
	}

	private static Status[] _performUpgrade(
		List<String> projectItems, List<String> projectActions, String runtimeName, IProgressMonitor monitor) {

		List<Status> retval = new ArrayList<>();

		int worked = 0;

		int workUnit = projectItems.size();
		int actionUnit = projectActions.size();
		int totalWork = 100;

		int perUnit = totalWork / (workUnit * actionUnit);

		monitor.beginTask("Upgrading Project ", totalWork);

		UpgradeProjectHandlerReader upgradeLiferayProjectActionReader = new UpgradeProjectHandlerReader();

		HashMap<String, AbstractUpgradeProjectHandler> actionMap = _getActionMap(
			upgradeLiferayProjectActionReader.getUpgradeActions());

		for (String projectItem : projectItems) {
			if (projectItem != null) {
				IProject project = ProjectUtil.getProject(projectItem);

				monitor.subTask("Upgrading project " + project.getName());

				for (String action : projectActions) {
					AbstractUpgradeProjectHandler upgradeLiferayProjectAction = actionMap.get(action);

					Status status = upgradeLiferayProjectAction.execute(project, runtimeName, monitor, perUnit);

					retval.add(status);

					worked = worked + totalWork / (workUnit * actionUnit);

					monitor.worked(worked);
				}
			}
		}

		return retval.toArray(new Status[0]);
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}