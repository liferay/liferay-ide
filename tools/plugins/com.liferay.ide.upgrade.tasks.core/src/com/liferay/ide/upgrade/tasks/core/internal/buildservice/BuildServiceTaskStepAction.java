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

package com.liferay.ide.upgrade.tasks.core.internal.buildservice;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionDoneEvent;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@Component(
	property = {"id=build_services", "order=2", "stepId=build_services", "title=Build Services"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class BuildServiceTaskStepAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		List<IProject> projects = _resourceSelection.selectProjects(
			"Select Lifreay Service Builder Project", false, new SelectableServiceBuilderProjectFilter());

		if (projects.isEmpty()) {
			return Status.CANCEL_STATUS;
		}

		Job buildServiceJob = new Job("BuildServiceJob") {

			public IStatus run(IProgressMonitor monitor) {
				try {
					for (IProject project : projects) {
						ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

						if (liferayProject != null) {
							IProjectBuilder builder = liferayProject.adapt(IProjectBuilder.class);

							builder.buildService(monitor);
						}
					}
				}
				catch (CoreException ce) {
				}

				_upgradePlanner.dispatch(new UpgradeTaskStepActionDoneEvent(BuildServiceTaskStepAction.this));

				return Status.OK_STATUS;
			}

		};

		buildServiceJob.schedule();

		return Status.OK_STATUS;
	}

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}