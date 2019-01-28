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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.FileProblems;
import com.liferay.ide.upgrade.plan.core.JavaProjectsSelectionTaskStep;
import com.liferay.ide.upgrade.plan.core.MigrationProblemsContainer;
import com.liferay.ide.upgrade.plan.core.Problem;
import com.liferay.ide.upgrade.plan.core.ProjectProblems;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepDoneEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.plan.core.util.UpgradeAssistantSettingsUtil;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.Migration;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=find_upgrade_problems", "requirement=recommended", "order=200", "taskId=find_upgrade_problems",
		"title=Find Upgrade Problems"
	},
	service = UpgradeTaskStep.class
)
public class FindUpgradeProblemsTaskStep extends JavaProjectsSelectionTaskStep implements UpgradeListener {

	@Override
	public IStatus execute(IProject[] projects, IProgressMonitor progressMonitor) {

		// TODO need to run finding upgrade changes by Upgrade Plan

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext context = bundle.getBundleContext();

		List<ProjectProblems> probjectProblemsList = new ArrayList<>();

		List<String> versions = new ArrayList<>();

		versions.add("7.0");
		versions.add("7.1");

		Job job = new Job("Finding migration problems...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Stream.of(
					projects
				).forEach(
					project -> {
						IPath location = project.getLocation();

						File searchFile = location.toFile();

						ServiceReference<Migration> serviceReference = context.getServiceReference(Migration.class);

						Migration migration = context.getService(serviceReference);

						List<Problem> problems = migration.findProblems(searchFile, versions, monitor);

						if (ListUtil.isNotEmpty(problems)) {
							FileProblems[] fileProblems = _getFileProblems(problems);

							ProjectProblems projectProblems = new ProjectProblems();

							projectProblems.setProjectName(project.getName());
							projectProblems.setFileProblems(fileProblems);

							probjectProblemsList.add(projectProblems);

							MigrationProblemsContainer container = new MigrationProblemsContainer();

							container.setProblemsArray(probjectProblemsList.toArray(new ProjectProblems[0]));

							try {
								UpgradeAssistantSettingsUtil.setObjectToStore(
									MigrationProblemsContainer.class, container);
							}
							catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
					}
				);

				getUpgradePlanner().dispatch(new UpgradeTaskStepDoneEvent());

				return Status.OK_STATUS;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {

		// TODO Auto-generated method stub

	}

	private FileProblems[] _getFileProblems(List<Problem> problems) {
		Map<File, FileProblems> fileProblemsMap = new HashMap<>();

		for (Problem problem : problems) {
			FileProblems fileProblem = fileProblemsMap.get(problem.getFile());

			if (fileProblem == null) {
				fileProblem = new FileProblems();
			}

			fileProblem.addProblem(problem);
			fileProblem.setFile(problem.getFile());

			fileProblemsMap.put(problem.getFile(), fileProblem);
		}

		Collection<FileProblems> values = fileProblemsMap.values();

		return values.toArray(new FileProblems[0]);
	}

}