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

package com.liferay.ide.ui.liferay.action;

import com.liferay.ide.ui.liferay.UIAction;
import com.liferay.ide.ui.swtbot.condition.CancelIvyJobCondition;
import com.liferay.ide.ui.swtbot.condition.CancelValidateJobCondition;
import com.liferay.ide.ui.swtbot.condition.CloseProjectJobCondition;
import com.liferay.ide.ui.swtbot.condition.NoRunningJobsCondition;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class JobAction extends UIAction {

	public JobAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	public void waitForCancelIvy() {
		bot.waitUntil(_cancelIvyJobCondition, 30000);
	}

	public void waitForCancelValidate(String projectName) {
		bot.waitUntil(new CancelValidateJobCondition(projectName), 120 * 1000);
	}

	public void waitForCloseProject() {
		bot.waitUntil(_closeProjectJobCondition, 30000);
	}

	public void waitForNoRunningJobs() {
		bot.waitUntil(_noRunningJobsCondition, 60 * 1000);
	}

	private final CancelIvyJobCondition _cancelIvyJobCondition = new CancelIvyJobCondition();
	private final CloseProjectJobCondition _closeProjectJobCondition = new CloseProjectJobCondition();
	private final NoRunningJobsCondition _noRunningJobsCondition = new NoRunningJobsCondition();

}