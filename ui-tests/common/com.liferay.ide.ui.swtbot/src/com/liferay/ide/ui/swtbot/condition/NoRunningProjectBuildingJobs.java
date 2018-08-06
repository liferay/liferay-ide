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

package com.liferay.ide.ui.swtbot.condition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Charles Wu
 */
public class NoRunningProjectBuildingJobs extends NoRunningJobsCondition {

	public NoRunningProjectBuildingJobs() {
	}

	@Override
	protected List<Job> checkRunningJobs() {
		List<Job> runningJobs = new ArrayList<>();

		IJobManager jobManager = Job.getJobManager();

		Job[] jobs = jobManager.find(family);

		for (Job job : jobs) {
			String jobName = job.getName();

			if ((job.getProperty(_liferayProjectJob) != null) || jobName.equals("Updating Maven Dependencies")) {
				runningJobs.add(job);
			}
		}

		return runningJobs;
	}

	private final QualifiedName _liferayProjectJob = new QualifiedName("com.liferay.ide.core", "LIFERAY_PROJECT_JOB");

}