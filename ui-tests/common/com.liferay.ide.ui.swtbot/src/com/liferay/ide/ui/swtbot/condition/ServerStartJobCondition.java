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

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Terry Jia
 */
public class ServerStartJobCondition extends WaitForSingleJob {

	public ServerStartJobCondition(String serverName) {
		super(null, "Server starting");

		_serverName = serverName;
	}

	@Override
	public String getJobName() {
		return "Starting " + _serverName;
	}

	@Override
	public boolean test() {
		IJobManager jobManager = Job.getJobManager();

		Job[] jobs = jobManager.find(family);

		for (Job job : jobs) {
			if (getJobName().equals(job.getName())) {
				return false;
			}
		}

		return true;
	}

	private String _serverName;

}