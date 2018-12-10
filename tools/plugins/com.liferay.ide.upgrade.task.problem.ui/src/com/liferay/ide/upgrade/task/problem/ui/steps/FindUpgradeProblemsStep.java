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

package com.liferay.ide.upgrade.task.problem.ui.steps;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.JavaProjectsUpgradeTaskStep;
import com.liferay.ide.upgrade.task.problem.api.FileProblems;
import com.liferay.ide.upgrade.task.problem.api.Migration;
import com.liferay.ide.upgrade.task.problem.api.MigrationProblemsContainer;
import com.liferay.ide.upgrade.task.problem.api.Problem;
import com.liferay.ide.upgrade.task.problem.api.ProjectProblems;
import com.liferay.ide.upgrade.task.problem.ui.util.MigrationUtil;
import com.liferay.ide.upgrade.task.problem.ui.util.UpgradeAssistantSettingsUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
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
@Component(properties = "OSGI-INF/FindUpgradeProblemsStep.properties", service = UpgradeTaskStep.class)
public class FindUpgradeProblemsStep extends JavaProjectsUpgradeTaskStep {

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
							FileProblems[] fileProblems = MigrationUtil.getFileProblems(problems);

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

							UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");
						}
					}
				);

				return Status.OK_STATUS;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}

}