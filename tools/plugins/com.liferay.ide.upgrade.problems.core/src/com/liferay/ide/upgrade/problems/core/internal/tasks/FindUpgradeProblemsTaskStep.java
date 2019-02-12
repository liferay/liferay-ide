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

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepDoneEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStepStatus;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.FileUpgradeProblem;

import java.io.File;

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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=find_upgrade_problems", "requirement=recommended", "order=2", "taskId=find_upgrade_problems",
		"title=Find Upgrade Problems"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class FindUpgradeProblemsTaskStep extends BaseUpgradeTaskStep implements UpgradeListener {

	public IStatus execute(IProject[] projects, IProgressMonitor progressMonitor) {

		// TODO need to run finding upgrade changes by Upgrade Plan

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

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

						ServiceReference<FileMigration> serviceReference = bundleContext.getServiceReference(
							FileMigration.class);

						FileMigration fileMigration = bundleContext.getService(serviceReference);

						List<FileUpgradeProblem> fileUpgradeProblems = fileMigration.findProblems(
							searchFile, versions, monitor);

						System.out.println(fileUpgradeProblems.size());
					}
				);

				_upgradePlanner.dispatch(new UpgradeTaskStepDoneEvent(FindUpgradeProblemsTaskStep.this));

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
	}

	@Reference
	private UpgradePlanner _upgradePlanner;

}