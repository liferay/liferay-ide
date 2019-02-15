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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepAction;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepActionDoneEvent;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.tasks.core.ResourceSelection;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {"id=find_upgrade_problems", "order=1", "stepId=find_upgrade_problems", "title=Find Upgrade Problems"},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStepAction.class
)
public class FindUpgradeProblemsTaskStepAction extends BaseUpgradeTaskStepAction {

	@Override
	public IStatus perform() {
		List<IProject> projects = _resourceSelection.selectProjects("select projects", true);

		List<String> versions = new ArrayList<>();

		versions.add("7.0");
		versions.add("7.1");

		Job job = new Job("Finding migration problems...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				UpgradePlan upgradePlan = _upgradePlanner.getCurrentUpgradePlan();

				Stream<IProject> stream = projects.stream();

				stream.forEach(
					project -> {
						File searchFile = FileUtil.getFile(project);

						List<UpgradeProblem> upgradeProblems = _fileMigration.findUpgradeProblems(
							searchFile, versions, monitor);

						upgradePlan.addUpgradeProblems(upgradeProblems);
					});

				_upgradePlanner.dispatch(new UpgradeTaskStepActionDoneEvent(FindUpgradeProblemsTaskStepAction.this));

				return Status.OK_STATUS;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}

	@Reference
	private FileMigration _fileMigration;

	@Reference
	private ResourceSelection _resourceSelection;

	@Reference
	private UpgradePlanner _upgradePlanner;

}