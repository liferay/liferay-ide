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

import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Terry Jia
 */
public class NoRunningJobsCondition extends JobCondition {

	public NoRunningJobsCondition() {
		super(null, "No Running Jobs");
	}

	@Override
	public String getFailureMessage() {
		Job[] jobs = _checkRunningJobs().toArray(new Job[0]);

		StringBuilder sb = new StringBuilder();

		sb.append("The following jobs are still running:");

		for (Job job : jobs) {
			sb.append(sb.append(job.getName()));
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public boolean test() {
		if (_checkRunningJobs().size() == 0) {
			return true;
		}

		return false;
	}

	private List<Job> _checkRunningJobs() {
		Job[] jobs = Job.getJobManager().find(family);

		List<Job> runningJobs = new ArrayList<>();

		for (Job job : jobs) {
			boolean found = false;
			String name = job.getName();
			if(name.equals("Refreshing server adapter list")) {
				job.cancel();
			}

			for (String excludedJob : _excludedJobs) {
				if (name.equals(excludedJob) || job.getState() == Job.SLEEPING) {
					found = true;

					break;
				}
			}

			if (!found) {
				runningJobs.add(job);
			}
		}

		return runningJobs;
	}

	private final String[] _excludedJobs = {
		"Open Notification Job", "Activity Monitor Job", "Task List Save Job", "Git Repository Change Scanner",
		"Workbench Auto-Save Job", "Compacting resource model", "Synchronizing Relevant Tasks",
		"Periodic workspace save.", "Synchronizing Task List", "Task Data Snapshot", "File Transport Reader",
		"File Transport Cancel Handler", "Process Console Input Job"
	};

}